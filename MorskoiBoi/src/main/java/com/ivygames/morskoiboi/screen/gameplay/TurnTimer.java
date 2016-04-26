package com.ivygames.morskoiboi.screen.gameplay;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

class TurnTimer extends AsyncTask<Void, Integer, Void> {
    private static final int RESOLUTION = 1000;

    private volatile int mTimeout;
    @NonNull
    private final TimerListener mListener;

    /**
     * @param timeout timeout in milliseconds
     */
    TurnTimer(int timeout, @NonNull TimerListener listener) {
        mTimeout = timeout;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.setCurrentTime(mTimeout);
    }

    @Override
    protected Void doInBackground(Void... params) {
        currentThread().setName("turn_timer");
        Ln.v("timer started for " + mTimeout + "ms");
        while (!currentThread().isInterrupted()) {
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
        mTimeout -= RESOLUTION;
        if (mTimeout > 0) {
            publishProgress(mTimeout);
        } else {
            publishProgress(0);
            currentThread().interrupt();
        }
    }



    public int getTimeLeft() {
        return mTimeout;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mListener.setCurrentTime(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (!isCancelled()) {
            Ln.d("timer finished - transferring turn");
            mListener.onTimerExpired();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @NonNull
    private Thread currentThread() {
        return Thread.currentThread();
    }
}
