package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Cell;

import org.commons.logger.Ln;

// TODO: just keep journal of shots with times
public class ScoreStatistics {
    private static final int INITIAL_SHELLS_NUMBER = 100; // 10 * 10

    private int mShells = INITIAL_SHELLS_NUMBER;
    private long mTimeSpent;
    private boolean mLastShotWasKill;
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

    public void updateWithNewShot(boolean kill, @NonNull Cell cell) {
        mShells--;

        // used for bonus scores calculation
        if (kill && mLastShotWasKill) {
            mCombo++;
            Ln.d("combo! " + mCombo);
        }

        if (cell == Cell.MISS) {
            mLastShotWasKill = false;
        } else if (kill) {
            Ln.v("sank");
            mLastShotWasKill = true;
        }
    }

    @Override
    public String toString() {
        return "[shells=" + mShells + ", timeSpent=" + mTimeSpent + ", lastShip=" + mLastShotWasKill + ", combo=" + mCombo + "]";
    }

}
