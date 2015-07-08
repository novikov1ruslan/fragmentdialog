package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.utils.UiUtils;

import org.commons.logger.Ln;

import java.util.PriorityQueue;

public class SetupBoardView extends BaseBoardView {

    private static final long LONG_PRESS_DELAY = 1000;

    // the following are drawn at the selection area
    /**
     * ship displayed at the top of the screen (selection area)
     */
    private Ship mCurrentShip;
    private final Rect mShipSelectionRect;
    private final Rect mShipDisplayRect;
    private PriorityQueue<Ship> mShips;

    // the following used during aiming
    /**
     * currently picked ship (awaiting to be placed)
     */
    private Ship mPickedShip;
    private final Rect mPickedShipRect;
    private int mAimI;
    private int mAimJ;

    /**
     * needed to perform double clicks on the ships
     */
    private PickShipTask mLongPressTask;
    private final Handler mHandler = new Handler();
    private final int mTouchSlop;
    private final Paint mConflictCellPaint;

    private final Bitmap mAircraftCarrier;
    private final Bitmap mBattleship;
    private final Bitmap mDestroyer;
    private final Bitmap mGunboat;

    private Bitmap mCurrentBitmap;
    private static TouchState mTouchState = new TouchState();
    private int mTouchX;
    private int mTouchY;

    public SetupBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mConflictCellPaint = UiUtils.newFillPaint(getResources(), R.color.conflict_cell);

        mPickedShipRect = new Rect();
        mShipSelectionRect = new Rect(0, 0, 0, 0);
        mShipDisplayRect = new Rect(0, 0, 0, 0);

        Bitmaps bitmaps = Bitmaps.getInstance();
        mAircraftCarrier = bitmaps.getBitmap(R.drawable.aircraft_carrier);
        mBattleship = bitmaps.getBitmap(R.drawable.battleship);
        mDestroyer = bitmaps.getBitmap(R.drawable.frigate);
        mGunboat = bitmaps.getBitmap(R.drawable.gunboat);

        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        Ln.v("touch slop = " + mTouchSlop);
    }

    private int getShipWidthInPx(Ship ship) {
        return ship.getSize() * mCellSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // paint invalid cells (ships that touch each other) and the ships themselves
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = mBoard.getCell(i, j);
                if (cell.isReserved()) {
                    float left = mBoardRect.left + i * mCellSize + 1;
                    float top = mBoardRect.top + j * mCellSize + 1;
                    float right = left + mCellSize;
                    float bottom = top + mCellSize;
                    if (cell.getProximity() > 8) {
                        canvas.drawRect(left + 1, top + 1, right, bottom, mConflictCellPaint);
                    }
                }
            }
        }

        if (mCurrentBitmap != null) {
            int displayLeft = mShipDisplayRect.centerX() - mCurrentBitmap.getWidth() / 2;
            int displayTop = mShipDisplayRect.centerY() - mCurrentBitmap.getHeight() / 2;
            canvas.drawBitmap(mCurrentBitmap, displayLeft, displayTop, null);
        }

        // draw the top of the screen (selection area)
        if (mCurrentShip != null) {
            int left = mShipSelectionRect.centerX() - getShipWidthInPx(mCurrentShip) / 2;
            int top = mShipSelectionRect.centerY() - mCellSize / 2;
            UiUtils.drawShip(canvas, mCurrentShip, left, top, mCellSize, mShipPaint);
        }

        if (mPickedShip != null) {
            // center dragged ship around touch point
            canvas.drawRect(mPickedShipRect, mShipPaint);

            // aiming
            int width = mPickedShip.isHorizontal() ? mPickedShip.getSize() : 1;
            int height = mPickedShip.isHorizontal() ? 1 : mPickedShip.getSize();
            drawAiming(canvas, mAimI, mAimJ, width, height);
        }
    }

    private void updateAim() {
        int shipInBoardCoordinatesX = mPickedShipRect.left - mBoardRect.left + mHalfCellSize;
        int shipInBoardCoordinatesY = mPickedShipRect.top - mBoardRect.top + mHalfCellSize;
        mAimI = shipInBoardCoordinatesX / mCellSize;
        mAimJ = shipInBoardCoordinatesY / mCellSize;
    }

    private void centerPickedShipAround(int touchX, int touchY) {
        int widthInPx = getShipWidthInPx(mPickedShip);
        int halfWidthInPx = getShipWidthInPx(mPickedShip) / 2;
        boolean isHorizontal = mPickedShip.isHorizontal();
        mPickedShipRect.left = touchX - (isHorizontal ? halfWidthInPx : mHalfCellSize);
        mPickedShipRect.top = touchY - (isHorizontal ? mHalfCellSize : halfWidthInPx);
        mPickedShipRect.right = mPickedShipRect.left + (isHorizontal ? widthInPx : mCellSize);
        mPickedShipRect.bottom = mPickedShipRect.top + (isHorizontal ? mCellSize : widthInPx);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean processed = mTouchState.onTouchEvent(event);
        mTouchX = mTouchState.getTouchX();
        mTouchY = mTouchState.getTouchY();

        if (mPickedShip != null) {
            centerPickedShipAround(mTouchX, mTouchY);
            updateAim();
        }

        switch (mTouchState.getTouchAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mLongPressTask != null && hasMovedBeyondThreshold()) {
                    runLongPressTask();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (mShipSelectionRect.contains(mTouchX, mTouchY)) {
                    tryPickingNewShip();
                } else if (mBoard.containsCell(getCellX(), getCellY())) {
                    scheduleNewPickTask(getCellX(), getCellY());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mLongPressTask != null) {
                    rotateShip();
                } else if (mPickedShip != null) {
                    dropPickedShip();
                }
                break;
            default:
                cancelLongPressTask();
                break;
        }

        invalidate();

        return processed;
    }

    private int getCellY() {
        return (mTouchY - mBoardRect.top) / mCellSize;
    }

    private int getCellX() {
        return (mTouchX - mBoardRect.left) / mCellSize;
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

    private void rotateShip() {
        cancelLongPressTask();
        mBoard.rotateShipAt(getCellX(), getCellY());
    }

    /**
     * @return true if succeeded to put down currently picked-up ship
     */
    private boolean tryPutPickedShip() {
        if (mBoard.canPutShipAt(mPickedShip, mAimI, mAimJ)) {
            mBoard.putShipAt(mPickedShip, mAimI, mAimJ);
            return true;
        }
        return false;
    }

    private void scheduleNewPickTask(int i, int j) {
        mLongPressTask = new PickShipTask(i, j, mTouchX, mTouchY);
        Ln.v("scheduling long press task: " + mLongPressTask);
        mHandler.postDelayed(mLongPressTask, LONG_PRESS_DELAY);
    }

    private void tryPickingNewShip() {
        mPickedShip = mShips.poll();
        if (mPickedShip == null) {
            Ln.v("no ships to pick");
        } else {
            mCurrentShip = null;
            centerPickedShipAround(mTouchX, mTouchY);
            updateAim();
            Ln.v(mPickedShip + " picked from stack, stack: " + mShips);
        }
    }

    private void returnPickedShipToPool() {
        if (!mPickedShip.isHorizontal()) {
            mPickedShip.rotate();
        }
        mShips.add(mPickedShip);
    }

    private void runLongPressTask() {
        Runnable task = mLongPressTask;
        cancelLongPressTask();
        task.run();
    }

    private boolean hasMovedBeyondThreshold() {
        int dX = mLongPressTask.getTouchX() - mTouchX;
        int dY = mLongPressTask.getTouchY() - mTouchY;
        return Math.sqrt(dX * dX + dY * dY) > mTouchSlop;
    }

    private void cancelLongPressTask() {
        Ln.v("cancelling long press task: " + mLongPressTask);
        mHandler.removeCallbacks(mLongPressTask);
        mLongPressTask = null;
    }

    private Bitmap getBitmapForSize(int size) {
        switch (size) {
            case 4:
                return mAircraftCarrier;
            case 3:
                return mBattleship;
            case 2:
                return mDestroyer;
            case 1:
            default:
                return mGunboat;
        }
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

        // calculate mShipSelectionRect (it starts from left=0, top=0)
        mShipSelectionRect.right = w / 2;
        mShipSelectionRect.bottom = h / 4;

        // calculate mShipDisplayRect (it starts from top=0)
        mShipDisplayRect.left = mShipSelectionRect.right + 1;
        mShipDisplayRect.right = w;
        mShipDisplayRect.bottom = mShipSelectionRect.bottom;

        calculateBoardRect(w, h - mShipSelectionRect.height(), 0, mShipDisplayRect.height());
    }

    private void setCurrentShip(Ship ship) {
        mCurrentShip = ship;
        if (mCurrentShip == null) {
            mCurrentBitmap = null;
        } else {
            mCurrentBitmap = getBitmapForSize(mCurrentShip.getSize());
        }
    }

    public void setFleet(PriorityQueue<Ship> ships) {
        Ln.v(ships);
        mShips = ships;
        setCurrentShip(mShips.peek());

        invalidate();
    }

    public boolean isSet() {
        boolean set = mShips.isEmpty() && mPickedShip == null && mBoard.getInvalidCells().isEmpty();
        int size = mBoard.getShips().size();
        if (set && size != 10) {
            Ln.e("wrong board size = " + size);
            set = false;
        }
        return set;
    }

    @Override
    public String toString() {
        return super.toString() + '\n' + mShips.toString();
    }

    private class PickShipTask implements Runnable {

        private final int mI;
        private final int mJ;
        private final int mTouchX;
        private final int mTouchY;

        public PickShipTask(int i, int j, int touchX, int touchY) {
            mI = i;
            mJ = j;
            mTouchX = touchX;
            mTouchY = touchY;
        }

        @Override
        public void run() {
            mLongPressTask = null;
            mPickedShip = mBoard.removeShipFrom(mI, mJ);
            if (mPickedShip != null) {
                centerPickedShipAround(mTouchX, mTouchY);
                updateAim();
            }
            invalidate();
        }

        public int getTouchX() {
            return mTouchX;
        }

        public int getTouchY() {
            return mTouchY;
        }

        @Override
        public String toString() {
            return "PressTask [i=" + mI + ", j=" + mJ + "]";
        }
    }
}
