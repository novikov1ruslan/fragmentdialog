package com.ivygames.morskoiboi.screen.boardsetup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
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

    // the following are drawn at the selection area
    /**
     * ship displayed at the top of the screen (selection area)
     */
    private Ship mCurrentShip;

    private PriorityQueue<Ship> mShips;

    // the following used during aiming
    /**
     * currently picked ship (awaiting to be placed)
     */
    private Ship mPickedShip;

    /**
     * needed to perform double clicks on the ships
     */
    private PickShipTask mPickShipTask;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final int mTouchSlop;

    private Bitmap mCurrentBitmap;
    private final Rules mRules = RulesFactory.getRules();
    private Vector2 mAim = Vector2.get(-1, -1);

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

        // paint invalid cells (ships that touch each other) and the ships themselves
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Cell cell = mBoard.getCell(i, j);
                if (cell.isReserved()) {

                    if (mRules.isCellConflicting(cell)) {
                        getRenderer().renderConflictingCell(canvas, getPresenter().getRectFor(i, j));
                    }
                }
            }
        }

        if (mCurrentBitmap != null) {
            Point center = getPresenter().getShipDisplayAreaCenter();
            int displayLeft = center.x - mCurrentBitmap.getWidth() / 2;
            int displayTop = center.y - mCurrentBitmap.getHeight() / 2;
            canvas.drawBitmap(mCurrentBitmap, displayLeft, displayTop, null);
        }

        drawScreenTop(canvas);

//        getRenderer().render(canvas, mTouchX, mTouchY);
    }

    private void drawScreenTop(Canvas canvas) {
        if (mCurrentShip != null) {
            Point p = getPresenter().getTopLeftPointInTopArea(mCurrentShip.getSize());
            mRenderer.drawShip(canvas, mPresenter.getRectForShip(mCurrentShip, p), mShipPaint);
        }

        if (mPickedShip != null) {
            // center dragged ship around touch point
            canvas.drawRect(getPresenter().getPickedShipRect(), mShipPaint);

            // aiming
            if (mBoard.containsCell(mAim)) {
                Aiming aiming = getPresenter().getAimingForPickedShip(mAim, mPickedShip);
                mRenderer.render(canvas, aiming, mAimingPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (mPickedShip != null) {
            mAim = getPresenter().getAimForShip(mPickedShip, event);
        }

        processMotionEvent(event);

        invalidate();

        return true;
    }

    private void processMotionEvent(MotionEvent event) {
        int i = getTouchI(event);
        int j = getTouchJ(event);
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
                    mPickedShip = mShips.poll();
                    if (mPickedShip == null) {
                        Ln.v("no ships to pick");
                    } else {
                        mCurrentShip = null;
                        mAim = getPresenter().getAimForShip(mPickedShip, event);
                        Ln.v(mPickedShip + " picked from stack, stack: " + mShips);
                    }
                } else if (mBoard.containsCell(i, j)) {
                    mPickShipTask = createNewPickTask(event);
                    Ln.v("scheduling long press task: " + mPickShipTask);
                    mHandler.postDelayed(mPickShipTask, LONG_PRESS_DELAY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPickShipTask != null) {
                    cancelLongPressTask();
                    mBoard.rotateShipAt(i, j);
                } else if (mPickedShip != null) {
                    dropShip(mPickedShip);
                    mPickedShip = null;
                }
                break;
            default:
                cancelLongPressTask();
                break;
        }
    }

    private int getTouchJ(MotionEvent event) {
        return getPresenter().getTouchJ((int) event.getY());
    }

    private int getTouchI(MotionEvent event) {
        return getPresenter().getTouchI((int) event.getX());
    }

    private void cancelLongPressTask() {
        Ln.v("cancelling long press task: " + mPickShipTask);
        mHandler.removeCallbacks(mPickShipTask);
        mPickShipTask = null;
    }

    private void dropShip(@NonNull Ship ship) {
        if (!tryPlaceShip(ship)) {
            returnShipToPool(ship);
        }

        // reselect current ship to display
        setCurrentShip();
    }

    private void setCurrentShip() {
        if (mShips.isEmpty()) {
            mCurrentShip = null;
            mCurrentBitmap = null;
        } else {
            mCurrentShip = mShips.peek();
            mCurrentBitmap = mRules.getBitmapForShipSize(mCurrentShip.getSize());
        }
    }

    /**
     * @return true if succeeded to put down currently picked-up ship
     */
    private boolean tryPlaceShip(@NonNull Ship ship) {
        if (mBoard.shipFitsTheBoard(ship, mAim)) {
            PlacementFactory.getAlgorithm().putShipAt(mBoard, ship, mAim.getX(), mAim.getY());
            return true;
        }
        return false;
    }

    private PickShipTask createNewPickTask(final MotionEvent event) {
        final int i = getTouchI(event);
        final int j = getTouchJ(event);
        return new PickShipTask(event, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPickShipTask = null;
                mPickedShip = mBoard.removeShipFrom(i, j);
                if (mPickedShip != null) {
                    mAim = getPresenter().getAimForShip(mPickedShip, event);
                }
                invalidate();
                return true;
            }
        });
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

    public void setFleet(PriorityQueue<Ship> ships) {
        Ln.v(ships);
        mShips = Validate.notNull(ships);

        setCurrentShip();

        invalidate();
    }

    @Override
    public String toString() {
        if (mShips == null) {
            Ln.e("no ships");
            return "no ships";
        }
        return super.toString() + '\n' + mShips.toString();
    }

}
