package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;
import android.view.View;

import org.commons.logger.Ln;

class PickShipTask implements Runnable {

    private final int mTouchX;
    private final int mTouchY;
    @NonNull
    private final View.OnLongClickListener mListener;

    PickShipTask(int x, int y, @NonNull View.OnLongClickListener listener) {
        mTouchX = x;
        mTouchY = y;
        mListener = listener;
    }

    @Override
    public void run() {
        mListener.onLongClick(null);
    }

    boolean hasMovedBeyondSlope(int x, int y, int slop) {
        int dX = mTouchX - x;
        int dY = mTouchY - y;
        double d = Math.sqrt(dX * dX + dY * dY);
        Ln.v(d + " " + slop);
        return d > slop;
    }

    @Override
    public String toString() {
        return "PressTask [x=" + mTouchX + ", y=" + mTouchY + "]#" + (hashCode() % 1000);
    }
}
