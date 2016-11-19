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
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.renderer.SetupBoardGeometryProcessor;
import com.ivygames.morskoiboi.renderer.SetupBoardRenderer;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;
import com.ivygames.morskoiboi.screen.view.TouchState;

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
    private final SetupBoardPresenter mPresenter;
    @NonNull
    private final TouchState mTouchState = new TouchState();
    @NonNull
    private Vector2 mPickedShipCoordinate = Vector2.INVALID_VECTOR;

    public SetupBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Ln.v("touch slop = " + mTouchSlop);

        mPresenter = new SetupBoardPresenter();
        mRenderer = (SetupBoardRenderer) super.mRenderer;
    }

    @NonNull
    @Override
    protected SetupBoardRenderer renderer() {
        SetupBoardGeometryProcessor processor =
                new SetupBoardGeometryProcessor(10, getResources().getDimension(R.dimen.ship_border));
        return new SetupBoardRenderer(getResources(), processor);
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
                if (BoardUtils.isCellConflicting(mBoard, i, j, mRules.allowAdjacentShips())) {
                    mRenderer.renderConflictingCell(canvas, i, j);
                }
            }
        }
    }

    private void drawDockedShip(Canvas canvas) {
        if (mPresenter.getDockedShip() != null) {
            mRenderer.drawDockedShip(canvas, mPresenter.getDockedShip());
        }
    }

    private void drawPickedShip(Canvas canvas) {
        Ship pickedShip = mPresenter.getPickedShip();
        if (pickedShip != null) {
            int x = mTouchState.getX();
            int y = mTouchState.getY();
            mRenderer.drawPickedShip(canvas, pickedShip, x, y);
            if (Board.contains(mPickedShipCoordinate)) {
                mRenderer.drawAiming(canvas, pickedShip, mPickedShipCoordinate);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        processMotionEvent(x, y, action);
        invalidate();
        return true;
    }

    private void processMotionEvent(int x, int y, int action) {
        Vector2 coordinate = Vector2.get(mRenderer.xToI(x), mRenderer.yToJ(y));
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (movedBeyondSlope(x, y)) {
                    pickSelectedShipFromBoard();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (mRenderer.isInDockArea(x, y)) {
                    mPresenter.pickDockedShip();
                } else if (Board.contains(coordinate) && mBoard.hasShipAt(coordinate)) {
                    Ln.v("board has ship at: " + coordinate);
                    schedulePickingShip(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pickUpScheduled()) {
                    cancelLongPressTask();
                    Placement.rotateShipAt(mBoard, coordinate.getX(), coordinate.getY());
                } else if (mPresenter.hasPickedShip()) {
                    Ln.v("dropping picked ship to: " + mPickedShipCoordinate);
                    mPresenter.dropShip(mBoard, mPickedShipCoordinate);
                }
                break;
            default:
                cancelLongPressTask();
                break;
        }

        if (mPresenter.getPickedShip() != null) {
            mPickedShipCoordinate = mRenderer.getPickedShipCoordinate(mPresenter.getPickedShip(), x, y);
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

    private void pickSelectedShipFromBoard() {
        mHandler.removeCallbacks(mPickShipTask);
        mPickShipTask.run();
        Ln.v("long press task canceled: " + mPickShipTask);
        mPickShipTask = null;
    }

    private boolean movedBeyondSlope(int x, int y) {
        return pickUpScheduled() && mPickShipTask.hasMovedBeyondSlope(x, y, mTouchSlop);
    }

    private PickShipTask createNewPickTask(final int x, final int y) {
        return new PickShipTask(x, y, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int i = mRenderer.xToI(x);
                int j = mRenderer.yToJ(y);
                Ln.v(mPickShipTask + ": picking ship from: [" + i + ", " + j + "]");
                Ship pickedShip = mPresenter.pickShipFromBoard(mBoard, i, j);
                if (pickedShip == null) {
                    Ln.e("picked ship is null");
                } else {
                    mPickedShipCoordinate = mRenderer.getPickedShipCoordinate(pickedShip, mTouchState.getX(), mTouchState.getY());
                }
                invalidate();
                mPickShipTask = null;
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
