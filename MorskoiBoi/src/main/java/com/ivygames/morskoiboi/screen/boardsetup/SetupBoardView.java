package com.ivygames.morskoiboi.screen.boardsetup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.Aiming;
import com.ivygames.morskoiboi.screen.view.BaseBoardRenderer;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.util.PriorityQueue;

public class SetupBoardView extends BaseBoardView {

    private static final long LONG_PRESS_DELAY = 1000;

    private PriorityQueue<Ship> mShips;

    /**
     * currently picked ship (awaiting to be placed)
     */
    private Ship mPickedShip;
    private Vector2 mAim = Vector2.get(-1, -1);

    /**
     * needed to perform double clicks on the ships
     */
    private PickShipTask mPickShipTask;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final int mTouchSlop;

    private final Rules mRules = RulesFactory.getRules();
    private final PlacementAlgorithm mPlacementAlgorithm = PlacementFactory.getAlgorithm();

    public SetupBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        Ln.v("touch slop = " + mTouchSlop);
    }

    @Override
    protected BaseBoardRenderer getRenderer() {
        if (mRenderer == null) {
            mRenderer = new BaseBoardRenderer(getResources());
        }

        return mRenderer;
    }

    @Override
    protected SetupBoardPresenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = new SetupBoardPresenter(10, getResources().getDimension(R.dimen.ship_border));
        }

        return (SetupBoardPresenter) mPresenter;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawConflictingCells(canvas);
        drawDockedShip(canvas);
        drawPickedShip(canvas);
//        getRenderer().render(canvas, mTouchX, mTouchY);
    }

    private void drawConflictingCells(Canvas canvas) {
        // paint invalid cells (ships that touch each other) and the ships themselves
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Cell cell = mBoard.getCell(i, j);
                if (mRules.isCellConflicting(cell)) {
                    getRenderer().renderConflictingCell(canvas, getPresenter().getRectFor(i, j));
                }
            }
        }
    }

    private void drawDockedShip(Canvas canvas) {
        if (getPresenter().getDockedShip() != null) {
            Bitmap bitmap = mRules.getBitmapForShipSize(getPresenter().getDockedShip().getSize());
            Point center = getPresenter().getShipDisplayAreaCenter();
            int displayLeft = center.x - bitmap.getWidth() / 2;
            int displayTop = center.y - bitmap.getHeight() / 2;
            canvas.drawBitmap(bitmap, displayLeft, displayTop, null);

            mRenderer.drawShip(canvas, getPresenter().getRectForDockedShip(), mShipPaint);
        }
    }

    private void drawPickedShip(Canvas canvas) {
        Rect shipRect = getPickedShipRect();
        if (shipRect != null) {
            canvas.drawRect(shipRect, mShipPaint);
        }

        Aiming aiming = getAiming();
        if (aiming != null) {
            mRenderer.render(canvas, aiming, mAimingPaint);
        }
    }

    public @Nullable Rect getPickedShipRect() {
        return mPickedShip == null ? null : getPresenter().getPickedShipRect();
    }

    public Aiming getAiming() {
        if (mPickedShip != null && mBoard.containsCell(mAim)) {
            return getPresenter().getAimingForPickedShip(mAim, mPickedShip);
        }

        return null;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        updateAim((int) event.getX(), (int) event.getY());

        processMotionEvent(event);

        invalidate();

        return true;
    }

    private void updateAim(int x, int y) {
        if (mPickedShip != null) {
            mAim = getPresenter().getAimForShip(mPickedShip, x, y);
        }
    }

    private void processMotionEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mPickShipTask != null && mPickShipTask.hasMovedBeyondSlope(event, mTouchSlop)) {
                    mHandler.removeCallbacks(mPickShipTask);
                    mPickShipTask.run();
                    mPickShipTask = null;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (getPresenter().isInShipSelectionArea(event)) {
                    pickDockedShipUp(x, y);
                } else if (isOnBoard(mBoard, x, y)) {
                    mPickShipTask = createNewPickTask(event);
                    Ln.v("scheduling long press task: " + mPickShipTask);
                    mHandler.postDelayed(mPickShipTask, LONG_PRESS_DELAY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPickShipTask != null) {
                    cancelLongPressTask();
                    rotateShipAt(mBoard, x, y);
                } else if (mPickedShip != null) {
                    dropShip(mBoard, mPickedShip);
                }
                break;
            default:
                cancelLongPressTask();
                break;
        }
    }

    private PickShipTask createNewPickTask(final MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        return new PickShipTask(event, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPickShipTask = null;
                pickShipFromBoard(mBoard, x, y);
                updateAim(x, y);
                invalidate();
                return true;
            }
        });
    }

    private void cancelLongPressTask() {
        Ln.v("cancelling long press task: " + mPickShipTask);
        mHandler.removeCallbacks(mPickShipTask);
        mPickShipTask = null;
    }

    private void pickShipFromBoard(@NonNull Board board, int x, int y) {
        final int i = getPresenter().getTouchI(x);
        final int j = getPresenter().getTouchJ(y);
        mPickedShip = board.removeShipFrom(i, j);
    }

    private void rotateShipAt(@NonNull Board board, int x, int y) {
        int i = getPresenter().getTouchI(x);
        int j = getPresenter().getTouchJ(y);
        board.rotateShipAt(i, j);
    }

    private boolean isOnBoard(@NonNull Board board, int x, int y) {
        int i = getPresenter().getTouchI(x);
        int j = getPresenter().getTouchJ(y);
        return board.containsCell(i, j);
    }

    private void pickDockedShipUp(int x, int y) {
        mPickedShip = mShips.poll();
        if (mPickedShip == null) {
            Ln.v("no ships to pick");
        } else {
            getPresenter().pickDockedShip();
            updateAim(x, y);
            Ln.v(mPickedShip + " picked from stack, stack: " + mShips);
        }
    }

    private void dropShip(@NonNull Board board, @NonNull Ship ship) {
        if (!tryPlaceShip(board, ship)) {
            returnShipToPool(ship);
        }

        getPresenter().setDockedShip(mShips);
        mPickedShip = null;
    }

    /**
     * @return true if succeeded to put down currently picked-up ship
     */
    private boolean tryPlaceShip(@NonNull Board board, @NonNull Ship ship) {
        if (board.shipFitsTheBoard(ship, mAim)) {
            mPlacementAlgorithm.putShipAt(board, ship, mAim.getX(), mAim.getY());
            return true;
        }
        return false;
    }

    private void returnShipToPool(@NonNull Ship ship) {
        if (!ship.isHorizontal()) {
            ship.rotate();
        }
        mShips.add(ship);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        mPresenter.measure(getMeasuredWidth(), getMeasuredHeight(), getHorizontalPadding(), getVerticalPadding());
    }

    public void setFleet(@NonNull PriorityQueue<Ship> ships) {
        Ln.v(ships);
        mShips = Validate.notNull(ships);

        getPresenter().setDockedShip(mShips);

        invalidate();
    }

    @Override
    public String toString() {
        if (mBoard == null) {
            Ln.e("no ships");
            return "no ships";
        }
        return super.toString() + '\n' + mBoard.toString();
    }

}
