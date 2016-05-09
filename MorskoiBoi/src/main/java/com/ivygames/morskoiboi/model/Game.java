package com.ivygames.morskoiboi.model;

import org.commons.logger.Ln;

public abstract class Game {
    public enum Type {
        VS_ANDROID, BLUETOOTH, INTERNET
    }

    public static final int SURRENDER_PENALTY_PER_DECK = 100;
    public static final int MIN_SURRENDER_PENALTY = 1000;

    private boolean mFinished;

    public abstract Type getType();

    public boolean finish() {
        if (mFinished) {
            Ln.w(getType() + " already finished");
            return true;
        }
        mFinished = true;
        return false;
    }

    public boolean hasFinished() {
        return mFinished;
    }

    /**
     * @return timeout in seconds
     */
    public abstract int getTurnTimeout();

    @Override
    public String toString() {
        return "[type=" + getType() + "]";
    }

}
