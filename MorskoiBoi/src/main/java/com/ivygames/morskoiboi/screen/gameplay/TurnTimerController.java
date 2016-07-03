package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class TurnTimerController {

    private static final int ALLOWED_SKIPPED_TURNS = 2;

    private TurnTimer mTurnTimer;

    private final int TURN_TIMEOUT;
    private TurnTimerFactory mTurnTimerFactory;

    private int mTimeLeft;

    private int mTimerExpiredCounter;

    @NonNull
    private final TimerListenerImpl mTimerListener;

    public TurnTimerController(int turnTimeout, @NonNull TurnTimerFactory turnTimerFactory) {
        TURN_TIMEOUT = turnTimeout;
        mTurnTimerFactory = turnTimerFactory;
        mTimerListener = new TimerListenerImpl();
        mTimeLeft = TURN_TIMEOUT;
    }

    public void start() {
        if (mTurnTimer != null) {
            reportException("start: already running");
            return;
        }

        Ln.d("starting timer");
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
        mTimeLeft = TURN_TIMEOUT;
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
            mTimeLeft = TURN_TIMEOUT;
            mTimerExpiredCounter++;
            if (mTimerExpiredCounter > ALLOWED_SKIPPED_TURNS) {
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
