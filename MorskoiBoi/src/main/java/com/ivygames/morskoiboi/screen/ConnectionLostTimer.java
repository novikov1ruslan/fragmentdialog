package com.ivygames.morskoiboi.screen;

import android.os.AsyncTask;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

class ConnectionLostTimer extends AsyncTask<Void, Void, Void> {
    private static final int RESOLUTION = 1000;

    private volatile int mTimeout;
    private final TimerListener mListener;

    interface TimerListener {
        void onTimerExpired();
    }

    /**
     * @param timeout timeout in milliseconds
     */
    ConnectionLostTimer(int timeout, TimerListener listener) {
        mTimeout = timeout;
        mListener = Validate.notNull(listener);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Thread.currentThread().setName("disconnect_timer");
        Ln.v("timer started for " + mTimeout + "ms");
        try {
            Thread.sleep(RESOLUTION);
        } catch (InterruptedException ie) {
            Ln.v("interrupted, cancelled=" + isCancelled());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (!isCancelled()) {
            Ln.d("timer finished - transferring turn");
            mListener.onTimerExpired();
        }
    }

}
