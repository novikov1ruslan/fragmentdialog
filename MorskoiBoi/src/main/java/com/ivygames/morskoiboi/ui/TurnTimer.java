package com.ivygames.morskoiboi.ui;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.ivygames.morskoiboi.GameplaySoundManager;
import com.ivygames.morskoiboi.ui.view.GameplayLayout;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

class TurnTimer extends AsyncTask<Void, Integer, Void> {
    private static final int RESOLUTION = 300;

    private volatile long mStartTime;
    private final int mTimeout;
    private final GameplayLayout mLayout;
    private final GameplaySoundManager mSoundManager;
    private final TimerListener mListener;

    interface TimerListener {
        void onTimerExpired();
    }

    TurnTimer(int timeout, GameplayLayout layout, TimerListener listener, GameplaySoundManager soundManager) {
        mTimeout = timeout;

        Validate.notNull(layout);
        mLayout = layout;

        Validate.notNull(listener);
        mListener = listener;

        Validate.notNull(soundManager);
        mSoundManager = soundManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mStartTime = SystemClock.elapsedRealtime();
        mLayout.setTime(mTimeout);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Thread.currentThread().setName("turn_timer");
        Ln.v("timer started for " + mTimeout + "ms");
        updateProgress();
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
        int timeLeft = getTimeLeft();
        if (shouldPlayAlarmSound() && mSoundManager.isSoundOn()) {
            mSoundManager.playAlarmSound();
        }

        if (timeLeft > 0) {
            publishProgress(timeLeft);
        } else {
            publishProgress(0);
            Thread.currentThread().interrupt();
        }
    }

    private boolean shouldPlayAlarmSound() {
        return getTimeLeft() <= GameplaySoundManager.ALARM_TIME_SECONDS && !mSoundManager.isAlarmPlaying();
    }

    public int getTimeLeft() {
        long d = SystemClock.elapsedRealtime() - mStartTime;
        return mTimeout - (int) d;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mLayout.setTime(values[0]);
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

        if (mSoundManager.isAlarmPlaying()) {
            Ln.v("timer canceled - stopping alarm");
            mSoundManager.stopAlarmSound();
        } else {
            Ln.v("timer canceled");
        }

    }

}
