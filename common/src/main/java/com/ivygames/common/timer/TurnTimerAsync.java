package com.ivygames.common.timer;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.example.games.basegameutils.BuildConfig;

import org.commons.logger.Ln;

class TurnTimerAsync implements TurnTimer {
    private static final int RESOLUTION = 1000;

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final UpdateTask mUpdateTask = new UpdateTask();
    @NonNull
    private final TimerUpdater mDelegate;

    /**
     * @param timeout timeout in milliseconds
     */
    TurnTimerAsync(int timeout, @NonNull TimerListener listener) {
        mDelegate = new TimerUpdater(timeout, RESOLUTION, listener);
    }

    @Override
    public void execute() {
        Ln.v("timer started for " + mDelegate.getRemainedTime() + "ms");
        update();
    }

    private void update() {
        mHandler.postDelayed(mUpdateTask, RESOLUTION);
    }

    @Override
    public void cancel() {
        Ln.v("cancelling timer");
        mHandler.removeCallbacks(mUpdateTask);
    }

    @Override
    public int getRemainedTime() {
        return mDelegate.getRemainedTime();
    }

    private class UpdateTask implements Runnable {
        @Override
        public void run() {
            mDelegate.tick();
            if (mDelegate.getRemainedTime() > 0) {
                if (BuildConfig.DEBUG) {
                    Ln.v("time left: " + mDelegate.getRemainedTime() + "ms");
                }
                update();
            }
        }
    }
}
