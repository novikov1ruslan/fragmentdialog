package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commons.logger.Ln;

public class ScoreStatistics {
    private static final int INITIAL_SHELLS_NUMBER = 100; // 10 * 10

    private int mShells = INITIAL_SHELLS_NUMBER;
    private long mTimeSpent;
    private Ship mLastShip;
    private int mCombo;

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

    public void updateWithNewShot(@Nullable Ship ship, @NonNull Cell cell) {
        mShells--;

        // used for bonus scores calculation
        if (ship != null && mLastShip != null) {
            mCombo++;
            Ln.d("combo! " + mCombo);
        }

        if (cell == Cell.MISS) {
            mLastShip = null;
        } else if (ship != null) {
            Ln.v("sank");
            mLastShip = ship;
        }
    }

    @Override
    public String toString() {
        return "[shells=" + mShells + ", timeSpent=" + mTimeSpent + ", lastShip=" + mLastShip + ", combo=" + mCombo + "]";
    }

}
