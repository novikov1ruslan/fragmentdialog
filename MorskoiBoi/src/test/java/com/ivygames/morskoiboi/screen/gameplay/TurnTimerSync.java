package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

public class TurnTimerSync implements TurnTimer {

    private static final int RESOLUTION = 1000;

    @NonNull
    private final TimerUpdater mDelegate;
    private int nTicks;
    private boolean cancelled;

    /**
     * @param timeout timeout in milliseconds
     */
    TurnTimerSync(int timeout, @NonNull TimerListener listener, int nTicks) {
        this.nTicks = nTicks;
        mDelegate = new TimerUpdater(timeout, RESOLUTION, listener);
    }

    @Override
    public void execute() {
        for (int i = 0; i < nTicks; i++) {
            if (cancelled) {
                return;
            } else {
                mDelegate.tick();
            }
        }
    }

    @Override
    public boolean cancel(boolean b) {
        cancelled = true;
        return false;
    }

    @Override
    public int getRemainedTime() {
        return mDelegate.getRemainedTime();
    }
}
