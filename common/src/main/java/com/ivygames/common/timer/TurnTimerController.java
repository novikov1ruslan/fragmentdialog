package com.ivygames.common.timer;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class TurnTimerController {
    private TurnTimer mTurnTimer;
    private int mTimeLeft;
    private int mTimerExpiredCounter;

    @NonNull
    private final TimerListenerImpl mTimerListener = new TimerListenerImpl();

    private final int mTurnTimeout;
    private final int mTurnsBeforeIdle;
    private final TurnTimerFactory mTurnTimerFactory;

    public TurnTimerController(int turnTimeout, int turnsBeforeIdle, @NonNull TurnTimerFactory turnTimerFactory) {
        mTurnTimeout = turnTimeout;
        mTurnsBeforeIdle = turnsBeforeIdle;
        mTurnTimerFactory = turnTimerFactory;

        mTimeLeft = mTurnTimeout;
    }

    public void start() {
        if (mTurnTimer != null) {
            reportException("start: already running");
            return;
        }

        Ln.v("starting timer");
        mTurnTimer = mTurnTimerFactory.newTimer(mTimeLeft, mTimerListener);
        mTurnTimer.execute();
    }

    public void pause() {
        if (mTurnTimer == null) {
            Ln.v("pause: not running");
            return;
        }

        mTurnTimer.cancel(true);
        mTimeLeft = mTurnTimer.getRemainedTime();
        Ln.d("timer pausing with " + mTimeLeft);
        processCancelRequest();
    }

    public void stop() {
        mTimerExpiredCounter = 0;
        if (mTurnTimer == null) {
            Ln.v("stop: not running");
            return;
        }

        mTurnTimer.cancel(true);
        mTimeLeft = mTurnTimeout;
        Ln.v("timer stopped");
        processCancelRequest();
    }

    private void processCancelRequest() {
        mTurnTimer = null;
        mTimerListener.onCanceled();
    }

    public void setListener(@NonNull TurnListener listener) {
        mTimerListener.setDelegate(listener);
    }

    private class TimerListenerImpl implements TimerListener {
        private TurnListener mTurnListener;

        @Override
        public void onTimerExpired() {
            mTurnTimer = null;
            mTimeLeft = mTurnTimeout;
            mTimerExpiredCounter++;
            if (mTimerExpiredCounter > mTurnsBeforeIdle) {
                mTurnListener.onPlayerIdle();
            } else {
                mTurnListener.onTimerExpired();
            }
        }

        @Override
        public void setCurrentTime(int time) {
            mTurnListener.setCurrentTime(time);
        }

        public void onCanceled() {
            mTurnListener.onCanceled();
        }

        public void setDelegate(@NonNull TurnListener listener) {
            mTurnListener = listener;
        }
    }
}
