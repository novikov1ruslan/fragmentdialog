package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class TurnTimerController {

    private TurnTimer mTurnTimer;

    private final int TURN_TIMEOUT;

    private int mTimeLeft;

    @NonNull
    private final TimerListenerImpl mTimerListener;

    public TurnTimerController(int turnTimeout) {
        TURN_TIMEOUT = turnTimeout;
        mTimerListener = new TimerListenerImpl();
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

    public void setListener(TimerListener listener) {
        mTimerListener.setDelegate(listener);
    }

    private class TimerListenerImpl implements TimerListener {
        @Nullable
        private TimerListener mDelegate;

        @Override
        public void onTimerExpired() {
            mTurnTimer = null;
            mTimeLeft = TURN_TIMEOUT;
            if (mDelegate != null) {
                mDelegate.onTimerExpired();
            }
        }

        @Override
        public void setCurrentTime(int time) {
            if (mDelegate != null) {
                mDelegate.setCurrentTime(time);
            }
        }

        @Override
        public void onCanceled() {
            if (mDelegate != null) {
                mDelegate.onCanceled();
            }
        }

        public void setDelegate(TimerListener listener) {
            mDelegate = listener;
        }
    }
}
