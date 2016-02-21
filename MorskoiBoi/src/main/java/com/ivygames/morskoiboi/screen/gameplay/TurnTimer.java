package com.ivygames.morskoiboi.screen.gameplay;

import android.os.AsyncTask;

import com.ivygames.morskoiboi.GameplaySoundManager;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

class TurnTimer extends AsyncTask<Void, Integer, Void> {
    private static final int RESOLUTION = 1000;

    private volatile int mTimeout;
    private final TimeConsumer mLayout;
    private final GameplaySoundManager mSoundManager;
    private final TimerListener mListener;

    interface TimerListener {
        void onTimerExpired();
    }

    /**
     * @param timeout timeout in milliseconds
     */
    TurnTimer(int timeout, TimeConsumer layout, TimerListener listener, GameplaySoundManager soundManager) {
        mTimeout = timeout;
        mLayout = Validate.notNull(layout);
        mListener = Validate.notNull(listener);
        mSoundManager = Validate.notNull(soundManager);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLayout.setCurrentTime(mTimeout);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Thread.currentThread().setName("turn_timer");
        Ln.v("timer started for " + mTimeout + "ms");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(RESOLUTION);
            } catch (InterruptedException ie) {
                Ln.v("interrupted");
                Thread.currentThread().interrupt();
                return null;
            }
            updateProgress();
        }
        return null;
    }

    private void updateProgress() {
        mTimeout -= RESOLUTION;
        if (shouldPlayAlarmSound(mTimeout)) {
            mSoundManager.playAlarmSound();
        }

        if (mTimeout > 0) {
            publishProgress(mTimeout);
        } else {
            publishProgress(0);
            Thread.currentThread().interrupt();
        }
    }

    private boolean shouldPlayAlarmSound(int timeLeft) {
        return timeLeft <= (GameplaySoundManager.ALARM_TIME_SECONDS * 1000) && !mSoundManager.isAlarmPlaying();
    }

    public int getTimeLeft() {
        return mTimeout;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mLayout.setCurrentTime(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (!isCancelled()) {
            Ln.d("timer finished - transferring turn");
            mListener.onTimerExpired();
            if (mSoundManager.isAlarmPlaying()) {
                Ln.v("timer expired - stopping alarm");
                mSoundManager.stopAlarmSound();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (mSoundManager.isAlarmPlaying()) {
            Ln.v("timer canceled - stopping alarm");
            mSoundManager.stopAlarmSound();
        } else {
            Ln.v("timer canceled");
        }

    }

}
