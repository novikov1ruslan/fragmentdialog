package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

class TimerUpdater {

    private volatile int mRemainedTime;
    @NonNull
    private final TimerListener mListener;
    private final int mResolution;

    /**
     * @param timeout timeout in milliseconds
     */
    TimerUpdater(int timeout, int resolution, @NonNull TimerListener listener) {
        mRemainedTime = timeout;
        mResolution = resolution;
        mListener = listener;

        mListener.setCurrentTime(mRemainedTime);
    }

    public void tick() {
        mRemainedTime -= mResolution;
        if (mRemainedTime > 0) {
            mListener.setCurrentTime(mRemainedTime);
        } else {
            mListener.setCurrentTime(0);
            Ln.d("timer expired");
            mListener.onTimerExpired();
        }
    }

    public int getRemainedTime() {
        return mRemainedTime;
    }
}
