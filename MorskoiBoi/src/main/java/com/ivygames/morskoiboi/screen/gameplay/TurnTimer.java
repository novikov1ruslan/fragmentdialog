package com.ivygames.morskoiboi.screen.gameplay;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

class TurnTimer extends AsyncTask<Void, Integer, Void> {
    private static final int RESOLUTION = 1000;

    private volatile int mRemainedTime;
    @NonNull
    private final TimerListener mListener;

    /**
     * @param timeout timeout in milliseconds
     */
    TurnTimer(int timeout, @NonNull TimerListener listener) {
        mRemainedTime = timeout;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.setCurrentTime(mRemainedTime);
    }

    @Override
    protected Void doInBackground(Void... params) {
        currentThread().setName("turn_timer");
        Ln.v("timer started for " + mRemainedTime + "ms");
        while (!currentThread().isInterrupted() && !isCancelled()) {
            try {
                Thread.sleep(RESOLUTION);
            } catch (InterruptedException ie) {
                Ln.v("interrupted");
                currentThread().interrupt();
                return null;
            }
            updateProgress();
        }
        return null;
    }

    private void updateProgress() {
        mRemainedTime -= RESOLUTION;
        if (mRemainedTime > 0) {
            publishProgress(mRemainedTime);
        } else {
            publishProgress(0);
            currentThread().interrupt();
        }
    }


    public int getRemainedTime() {
        return mRemainedTime;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mListener.setCurrentTime(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        // This method won't be invoked if the task was cancelled.
        super.onPostExecute(result);
        Ln.d("timer expired");
        mListener.onTimerExpired();
    }

    @NonNull
    private static Thread currentThread() {
        return Thread.currentThread();
    }
}
