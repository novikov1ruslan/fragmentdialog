package com.ivygames.morskoiboi.screen.boardsetup;

import android.view.View;

public class PickShipTask implements Runnable {

    private final int mTouchX;
    private final int mTouchY;
    private View.OnLongClickListener mListener;

    public PickShipTask(int x, int y, View.OnLongClickListener listener) {
        mTouchX = x;
        mTouchY = y;
        mListener = listener;
    }

    @Override
    public void run() {
        mListener.onLongClick(null);
    }

    public boolean hasMovedBeyondSlope(int x, int y, int slop) {
        int dX = mTouchX - x;
        int dY = mTouchY - y;
        return Math.sqrt(dX * dX + dY * dY) > slop;
    }

//    @Override
//    public String toString() {
//        return "PressTask [i=" + mI + ", j=" + mJ + "]";
//    }
}
