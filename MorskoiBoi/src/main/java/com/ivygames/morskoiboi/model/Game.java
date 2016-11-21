package com.ivygames.morskoiboi.model;

import com.ivygames.common.Finishable;

import org.commons.logger.Ln;

public abstract class Game implements Finishable {

    public static final int WIN_POINTS_SHOULD_BE_CALCULATED = -1;

    public static final int SURRENDER_PENALTY_PER_DECK = 100;
    public static final int MIN_SURRENDER_PENALTY = 1000;

    private boolean mFinished;

    public abstract boolean shouldNotifyOpponent();

    @Override
    public boolean finish() {
        if (mFinished) {
            Ln.w(this + " already finished");
            return true;
        }
        mFinished = true;
        return false;
    }

    /**
     * @return timeout in seconds
     */
    public abstract int getTurnTimeout();

    public abstract boolean isRemote();

    public abstract boolean supportsAchievements();
    
    public abstract int getWinPoints();

    public abstract boolean isPausable();
}
