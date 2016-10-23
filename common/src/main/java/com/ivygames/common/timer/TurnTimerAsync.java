package com.ivygames.common.timer;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

public class TurnTimerAsync implements TurnTimer {
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
    public boolean cancel(boolean b) {
        Ln.v("cancelling timer");
        mHandler.removeCallbacks(mUpdateTask);
        return true;
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
                Ln.v("time left: " + mDelegate.getRemainedTime() + "ms");
                update();
            }
        }
    }
}
