package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import com.ivygames.morskoiboi.utils.UiUtils;

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
    private final TouchState mTouchState = new TouchState();
    private int mTouchX;
    private int mTouchY;
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
                        getRenderer().renderConflictingCell(canvas, mPresenter.getInvalidRect(i, j));
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

        getRenderer().render(canvas, mTouchState);
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
                int width = mPickedShip.isHorizontal() ? mPickedShip.getSize() : 1;
                int height = mPickedShip.isHorizontal() ? 1 : mPickedShip.getSize();
                Aiming aiming = getPresenter().getAiming(mAim, width, height);
                mRenderer.render(canvas, aiming, mAimingPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        mTouchX = mTouchState.getTouchX();
        mTouchY = mTouchState.getTouchY();

        if (mPickedShip != null) {
            getPresenter().centerPickedShipAround(mTouchX, mTouchY, mPickedShip);
            mAim = getPresenter().getAim();
        }

        processMotionEvent(mTouchState.getTouchAction());

        invalidate();

        return true;
    }

    private void processMotionEvent(int event) {
        switch (event) {
            case MotionEvent.ACTION_MOVE:
                if (mPickShipTask != null && mPickShipTask.hasMovedBeyondSlope(mTouchX, mTouchY, mTouchSlop)) {
                    Runnable task = mPickShipTask;
                    cancelLongPressTask();
                    task.run();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (getPresenter().isInShipSelectionArea(mTouchX, mTouchY)) {
                    mPickedShip = mShips.poll();
                    tryPickingNewShip(mPickedShip);
                } else if (mBoard.containsCell(getCellX(), getCellY())) {
                    mPickShipTask = scheduleNewPickTask(getCellX(), getCellY());
                    Ln.v("scheduling long press task: " + mPickShipTask);
                    mHandler.postDelayed(mPickShipTask, LONG_PRESS_DELAY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPickShipTask != null) {
                    cancelLongPressTask();
                    mBoard.rotateShipAt(getCellX(), getCellY());
                } else if (mPickedShip != null) {
                    dropPickedShip();
                }
                break;
            default:
                cancelLongPressTask();
                break;
        }
    }

    private int getCellY() {
        return mPresenter.getCellY(mTouchY);
    }

    private int getCellX() {
        return mPresenter.getCellX(mTouchX);
    }

    /**
     * Tries to put {@link #mPickedShip} on the board. Returns it to the pool if fails. {@link #mCurrentShip} is re-taken from the pool.
     */
    private void dropPickedShip() {
        if (!tryPutPickedShip()) {
            returnPickedShipToPool();
        }

        // reselect current ship to display
        setCurrentShip(mShips.peek());

        mPickedShip = null;
    }

    /**
     * @return true if succeeded to put down currently picked-up ship
     */
    private boolean tryPutPickedShip() {
        if (mBoard.shipFitsTheBoard(mPickedShip, mAim)) {
            PlacementFactory.getAlgorithm().putShipAt(mBoard, mPickedShip, mAim.getX(), mAim.getY());
            return true;
        }
        return false;
    }

    private PickShipTask scheduleNewPickTask(final int i, final int j) {
        mPickShipTask = new PickShipTask(mTouchX, mTouchY, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPickShipTask = null;
                mPickedShip = mBoard.removeShipFrom(i, j);
                if (mPickedShip != null) {
                    getPresenter().centerPickedShipAround(mTouchX, mTouchY, mPickedShip);
                    mAim = getPresenter().getAim();
                }
                invalidate();
                return true;
            }
        });

        return mPickShipTask;
    }

    public void tryPickingNewShip(Ship mPickedShip) {
        if (mPickedShip == null) {
            Ln.v("no ships to pick");
        } else {
            mCurrentShip = null;
            getPresenter().centerPickedShipAround(mTouchX, mTouchY, mPickedShip);
            mAim = getPresenter().getAim();
            Ln.v(mPickedShip + " picked from stack, stack: " + mShips);
        }
    }

    private void returnPickedShipToPool() {
        if (!mPickedShip.isHorizontal()) {
            mPickedShip.rotate();
        }
        mShips.add(mPickedShip);
    }

    private void cancelLongPressTask() {
        Ln.v("cancelling long press task: " + mPickShipTask);
        mHandler.removeCallbacks(mPickShipTask);
        mPickShipTask = null;
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

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int hPadding = getHorizontalPadding();
        int vPadding = getVerticalPadding();
        getPresenter().measure(w, h, hPadding, vPadding);
    }

    private void setCurrentShip(Ship ship) {
        mCurrentShip = ship;
        if (mCurrentShip == null) {
            mCurrentBitmap = null;
        } else {
            mCurrentBitmap = RulesFactory.getRules().getBitmapForShipSize(mCurrentShip.getSize());
        }
    }

    public void setFleet(PriorityQueue<Ship> ships) {
        Ln.v(ships);
        mShips = Validate.notNull(ships);
        setCurrentShip(mShips.peek());

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
