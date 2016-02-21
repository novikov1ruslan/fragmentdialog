package com.ivygames.morskoiboi.screen.view;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.commons.logger.Ln;

public class TouchState {

    private int mTouchX;
    private int mTouchY;
    private int mTouchAction = MotionEvent.ACTION_UP;
    private int mDragStatus;

    public static final int START_DRAGGING = 1;
    private static final int STOP_DRAGGING = 0;

    public void setEvent(@NonNull MotionEvent event) {
        mTouchX = (int) event.getX();
        mTouchY = (int) event.getY();
        mTouchAction = event.getAction();
        if (mTouchAction == MotionEvent.ACTION_DOWN) {
            Ln.v("ACTION_DOWN: " + mTouchX + ":" + mTouchY);
            mDragStatus = START_DRAGGING;
        } else if (getTouchAction() == MotionEvent.ACTION_UP) {
            Ln.v("ACTION_UP: " + mTouchX + ":" + mTouchY);
            mDragStatus = STOP_DRAGGING;
        }
    }

    public int getTouchX() {
        return mTouchX;
    }

    public int getTouchY() {
        return mTouchY;
    }

    public int getTouchAction() {
        return mTouchAction;
    }

    public int getDragStatus() {
        return mDragStatus;
    }
}
