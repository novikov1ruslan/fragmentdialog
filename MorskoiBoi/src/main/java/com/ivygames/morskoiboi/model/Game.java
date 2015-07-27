package com.ivygames.morskoiboi.model;

import org.commons.logger.Ln;

import java.util.Collection;

public abstract class Game {
    public enum Type {
        VS_ANDROID, BLUETOOTH, INTERNET
    }

    private static final int INITIAL_SHELLS_NUMBER = 100; // 10 * 10

    public static final int SURRENDER_PENALTY_PER_DECK = 100;
    public static final int MIN_SURRENDER_PENALTY = 1000;

    private int mShells = INITIAL_SHELLS_NUMBER;
    private long mTimeSpent;
    private Ship mLastShip;
    private int mCombo;
    private boolean mFinished;

    public int getShells() {
        return mShells;
    }

    public int getCombo() {
        return mCombo;
    }

    public long getTimeSpent() {
        return mTimeSpent;
    }

    public void setTimeSpent(long millis) {
        Ln.d("time spent in game = " + millis);
        mTimeSpent = millis;
    }

    public abstract Type getType();

    public void updateWithNewShot(Ship ship, Cell cell) {
        mShells--;

        // used for bonus scores calculation
        if (ship != null && mLastShip != null) {
            mCombo++;
            Ln.d("combo! " + mCombo);
        }

        if (cell.isMiss()) {
            mLastShip = null;
        } else if (ship != null) {
            Ln.v("sank");
            mLastShip = ship;
        }
    }

    public void finish() {
        mFinished = true;
        finishMatch();
    }

    public boolean hasFinished() {
        return mFinished;
    }

    public void finishMatch() {

    }

    /**
     * @return timeout in seconds
     */
    public abstract int getTurnTimeout();

    public void clearState() {
        mShells = INITIAL_SHELLS_NUMBER;
        mTimeSpent = 0;
        mLastShip = null;
        mCombo = 0;
        Ln.d("game state cleared");
    }

    @Override
    public String toString() {
        return "[type=" + getType() + ", shells=" + mShells + ", timeSpent=" + mTimeSpent + ", lastShip=" + mLastShip + ", combo=" + mCombo + "]";
    }

}
