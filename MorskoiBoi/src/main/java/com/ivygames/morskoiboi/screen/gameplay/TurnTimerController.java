package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class TurnTimerController {

    private TurnTimer mTurnTimer;

    private final int TURN_TIMEOUT;

    private int mTimeLeft;

    @NonNull
    private final TimerListener mTimerListener;

    public TurnTimerController(int turnTimeout, @NonNull TimerListener listener) {
        TURN_TIMEOUT = turnTimeout;
        mTimerListener = new TimerListenerImpl(listener);
        mTimeLeft = TURN_TIMEOUT;
    }

    public void start() {
        if (mTurnTimer != null) {
            reportException("start: already running");
            return;
        }

        Ln.d("starting timer");
        mTurnTimer = new TurnTimer(mTimeLeft, mTimerListener);
        mTurnTimer.execute();
    }

    public void pause() {
        if (mTurnTimer == null) {
            reportException("pause: not running");
            return;
        }

        mTurnTimer.cancel(true);
        mTimeLeft = mTurnTimer.getRemainedTime();
        Ln.d("timer pausing with " + mTimeLeft);
        processCancelRequest();
    }

    public void stop() {
        if (mTurnTimer == null) {
            reportException("stop: not running");
            return;
        }

        mTurnTimer.cancel(true);
        mTimeLeft = TURN_TIMEOUT;
        Ln.v("timer stopped");
        processCancelRequest();
    }

    private void processCancelRequest() {
        mTurnTimer = null;
        mTimerListener.onCanceled();
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
            mTimeLeft = TURN_TIMEOUT;
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
