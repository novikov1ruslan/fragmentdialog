package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.commons.logger.Ln;

class TouchState {

    private static final int START_DRAGGING = 1;
    private static final int STOP_DRAGGING = 0;

    private int mTouchX;
    private int mTouchY;
    private int mTouchAction = MotionEvent.ACTION_UP;
    private int mDragStatus;

    void setEvent(@NonNull MotionEvent event) {
        mTouchX = (int) event.getX();
        mTouchY = (int) event.getY();
        mTouchAction = event.getAction();
        if (mTouchAction == MotionEvent.ACTION_DOWN) {
            Ln.v("DOWN: " + mTouchX + ":" + mTouchY);
            mDragStatus = START_DRAGGING;
        } else if (mTouchAction == MotionEvent.ACTION_UP) {
            Ln.v("UP: " + mTouchX + ":" + mTouchY);
            mDragStatus = STOP_DRAGGING;
        }
    }

    int getX() {
        return mTouchX;
    }

    int getY() {
        return mTouchY;
    }

    int getAction() {
        return mTouchAction;
    }

    boolean isDragging() {
        return mDragStatus == TouchState.START_DRAGGING;
    }

    @Override
    public String toString() {
        return "TouchState{" +
                "x=" + mTouchX +
                ", y=" + mTouchY +
                ", action=" + mTouchAction +
                ", drag=" + mDragStatus +
                '}';
    }
}
