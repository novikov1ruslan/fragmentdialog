package com.ivygames.morskoiboi.screen.boardsetup;

import android.view.MotionEvent;
import android.view.View;

public class PickShipTask implements Runnable {

    private final int mTouchX;
    private final int mTouchY;
    private View.OnLongClickListener mListener;

    public PickShipTask(MotionEvent event, View.OnLongClickListener listener) {
        mTouchX = (int) event.getX();
        mTouchY = (int) event.getY();
        mListener = listener;
    }

    @Override
    public void run() {
        mListener.onLongClick(null);
    }

    public boolean hasMovedBeyondSlope(MotionEvent event, int slop) {
        int dX = mTouchX - (int) event.getX();
        int dY = mTouchY - (int) event.getY();
        return Math.sqrt(dX * dX + dY * dY) > slop;
    }

//    @Override
//    public String toString() {
//        return "PressTask [i=" + mI + ", j=" + mJ + "]";
//    }
}
