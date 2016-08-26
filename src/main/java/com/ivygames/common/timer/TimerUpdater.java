package com.ivygames.common.timer;

import android.support.annotation.NonNull;

import com.ivygames.common.timer.TimerListener;

import org.commons.logger.Ln;

public class TimerUpdater {

    private volatile int mRemainedTime;
    @NonNull
    private final TimerListener mListener;
    private final int mResolution;

    /**
     * @param timeout timeout in milliseconds
     */
    public TimerUpdater(int timeout, int resolution, @NonNull TimerListener listener) {
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
