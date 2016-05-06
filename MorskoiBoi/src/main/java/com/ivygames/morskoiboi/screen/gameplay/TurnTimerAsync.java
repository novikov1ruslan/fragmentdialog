package com.ivygames.morskoiboi.screen.gameplay;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

class TurnTimerAsync extends AsyncTask<Void, Void, Void> implements TurnTimer {
    private static final int RESOLUTION = 1000;

    @NonNull
    private final TimerUpdater mDelegate;

    /**
     * @param timeout timeout in milliseconds
     */
    TurnTimerAsync(int timeout, @NonNull TimerListener listener) {
        mDelegate = new TimerUpdater(timeout, RESOLUTION, listener);
    }

    @Override
    protected Void doInBackground(Void... params) {
        workerThread().setName("turn_timer");
        Ln.v("timer started for " + mDelegate.getRemainedTime() + "ms");
        while (!workerThread().isInterrupted() && !isCancelled()) {
            try {
                Thread.sleep(RESOLUTION);
            } catch (InterruptedException ie) {
                Ln.v("interrupted");
                workerThread().interrupt();
                return null;
            }
            publishProgress();
        }
        return null;
    }

    @Override
    public void execute() {
        super.execute();
    }

    @Override
    public int getRemainedTime() {
        return mDelegate.getRemainedTime();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        mDelegate.tick();
        if (mDelegate.getRemainedTime() <= 0) {
            cancel(true);
        }
    }

    @NonNull
    private static Thread workerThread() {
        return Thread.currentThread();
    }
}
