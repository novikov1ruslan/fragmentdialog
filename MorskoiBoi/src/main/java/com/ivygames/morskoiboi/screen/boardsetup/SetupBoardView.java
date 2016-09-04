package com.ivygames.morskoiboi.screen.boardsetup;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;

import org.commons.logger.Ln;

import java.util.PriorityQueue;

public class SetupBoardView extends BaseBoardView {

    private static final long LONG_PRESS_DELAY = 1000;

    /**
     * needed to perform double clicks on the ships
     */
    private PickShipTask mPickShipTask;
    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final int mTouchSlop;
    @NonNull
    private final Rules mRules = Dependencies.getRules();

    @NonNull
    private final SetupBoardRenderer mRenderer;

    @NonNull
    private  SetupBoardPresenter mPresenter;

    public SetupBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Ln.v("touch slop = " + mTouchSlop);

        mRenderer = (SetupBoardRenderer) super.mRenderer;
    }

    @NonNull
    @Override
    protected SetupBoardRenderer renderer() {
        mPresenter = new SetupBoardPresenter(10, getResources().getDimension(R.dimen.ship_border));
        return new SetupBoardRenderer(getResources(), mPresenter);
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
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Cell cell = mBoard.getCell(i, j);
                if (mRules.isCellConflicting(cell)) {
                    mRenderer.renderConflictingCell(canvas, i, j);
                }
            }
        }
    }

    private void drawDockedShip(Canvas canvas) {
        mRenderer.drawDockedShip(canvas);
    }

    private void drawPickedShip(Canvas canvas) {
        mRenderer.drawPickedShip(canvas);

        mRenderer.drawAiming(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        if (mPresenter.getPickedShip() != null) {
            mRenderer.updatePickedGeometry(x, y);
        }
        processMotionEvent(x, y, action);
        invalidate();
        return true;
    }

    private void processMotionEvent(int x, int y, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (movedBeyondSlope(x, y)) {
                    pickShipFromBoard();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (mRenderer.isInDockArea(x, y)) {
                    mPresenter.pickDockedShip();
                    mRenderer.updatePickedGeometry(x, y);
                } else if (mPresenter.isOnBoard(x, y)) {
                    schedulePickingShip(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pickUpScheduled()) {
                    cancelLongPressTask();
                    mPresenter.rotateShipAt(mBoard, x, y);
                } else if (mPresenter.hasPickedShip()) {
                    mPresenter.dropShip(mBoard);
                }
                break;
            default:
                cancelLongPressTask();
                break;
        }
    }

    private boolean pickUpScheduled() {
        return mPickShipTask != null;
    }

    private void schedulePickingShip(int x, int y) {
        mPickShipTask = createNewPickTask(x, y);
        Ln.v("scheduling long press task: " + mPickShipTask);
        mHandler.postDelayed(mPickShipTask, LONG_PRESS_DELAY);
    }

    private void pickShipFromBoard() {
        mHandler.removeCallbacks(mPickShipTask);
        mPickShipTask.run();
        mPickShipTask = null;
    }

    private boolean movedBeyondSlope(int x, int y) {
        return pickUpScheduled() && mPickShipTask.hasMovedBeyondSlope(x, y, mTouchSlop);
    }

    private PickShipTask createNewPickTask(final int x, final int y) {
        return new PickShipTask(x, y, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPickShipTask = null;
                mPresenter.pickShipFromBoard(mBoard, x, y);
                mPresenter.touch(x, y);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    public void setFleet(@NonNull PriorityQueue<Ship> ships) {
        Ln.v(ships);
        mPresenter.setFleet(ships);
        invalidate();
    }

    public void notifyDataChanged() {
        mPresenter.setDockedShip();
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
