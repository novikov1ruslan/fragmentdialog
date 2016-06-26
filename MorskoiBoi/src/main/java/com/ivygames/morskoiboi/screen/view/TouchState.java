package com.ivygames.morskoiboi.screen.view;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.commons.logger.Ln;

public class TouchState {

    public static final int START_DRAGGING = 1;
    private static final int STOP_DRAGGING = 0;

    private int mTouchX;
    private int mTouchY;
    private int mTouchAction = MotionEvent.ACTION_UP;
    private int mDragStatus;

    public void setEvent(@NonNull MotionEvent event) {
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

    public int getX() {
        return mTouchX;
    }

    public int getY() {
        return mTouchY;
    }

    public int getAction() {
        return mTouchAction;
    }

    public int getDragStatus() {
        return mDragStatus;
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
