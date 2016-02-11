package com.ivygames.morskoiboi.ui.view;

import android.view.View;

public class PickShipTask implements Runnable {

    private final int mI;
    private final int mJ;
    private final int mTouchX;
    private final int mTouchY;
    private View.OnLongClickListener mListener;

    public PickShipTask(int i, int j, int touchX, int touchY, View.OnLongClickListener listener) {
        mI = i;
        mJ = j;
        mTouchX = touchX;
        mTouchY = touchY;
        mListener = listener;
    }

    @Override
    public void run() {
        mListener.onLongClick(null);
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
