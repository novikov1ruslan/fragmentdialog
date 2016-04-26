package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class TurnTimerController {

    private static final int READY_TO_START = 0;

    private int mTimeLeft = READY_TO_START;

    private TurnTimer mTurnTimer;

    private final int TURN_TIMEOUT;

    @NonNull
    private final TimerListener mTimerListener;

    public TurnTimerController(int turnTimeout, @NonNull TimerListener listener) {
        TURN_TIMEOUT = turnTimeout;
        mTimerListener = new TimerListenerImpl(listener);
    }

    public void startTurnTimer() {
        if (mTurnTimer != null) {
            reportException("already running");
            stopTurnTimer();
        }

        Ln.d("starting timer");
        mTurnTimer = new TurnTimer(TURN_TIMEOUT, mTimerListener);
        mTurnTimer.execute();
    }

    public void pauseTurnTimer() {
        if (mTurnTimer != null) {
            mTimeLeft = mTurnTimer.getTimeLeft();
            Ln.d("timer pausing with " + mTimeLeft);
            mTurnTimer.cancel(true);
            mTimerListener.onCanceled();
            mTurnTimer = null;
        }
    }

    public boolean isTimerPaused() {
        return mTimeLeft != READY_TO_START;
    }

    public void stopTurnTimer() {
        if (mTurnTimer != null) {
            mTurnTimer.cancel(true);
            mTimerListener.onCanceled();
            mTimeLeft = READY_TO_START;
            Ln.v("timer stopped");
            mTurnTimer = null;
        }
    }

    /**
     * only called for android game
     */
    public void resumeTurnTimer() {
        if (mTurnTimer != null) {
            String message = "already resumed";
            reportException(message);
            pauseTurnTimer();
        }

        Ln.v("resuming timer for " + mTimeLeft);
        mTurnTimer = new TurnTimer(mTimeLeft, mTimerListener);
        mTimeLeft = READY_TO_START;
        mTurnTimer.execute();
    }

    private class TimerListenerImpl implements TimerListener {
        @NonNull
        private final TimerListener mDelegate;

        private TimerListenerImpl(@NonNull TimerListener listener) {
            mDelegate = listener;
        }

        @Override
        public void onTimerExpired() {
            mTurnTimer = null;
            mTimeLeft = READY_TO_START;
            mDelegate.onTimerExpired();
        }

        @Override
        public void setCurrentTime(int time) {
            mDelegate.setCurrentTime(time);
        }

        @Override
        public void onCanceled() {
            mDelegate.onCanceled();
        }
    }
}
