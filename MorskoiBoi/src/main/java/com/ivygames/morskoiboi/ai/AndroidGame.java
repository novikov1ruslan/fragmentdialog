package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.Game;

import org.commons.logger.Ln;

public class AndroidGame extends Game {
    private static final int TURN_TIMEOUT = 2 * 60 * 1000;

    public AndroidGame() {
        Ln.v("new android game created");
    }

    @Override
    public boolean finish() {
        if (super.finish()) {
            return true;
        }
        Ln.d("finishing Android game - AI stopped");
        return true;
    }

    @Override
    public boolean shouldNotifyOpponent() {
        return false;
    }

    @Override
    public int getTurnTimeout() {
        return TURN_TIMEOUT;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean supportsAchievements() {
        return true;
    }

    @Override
    public int getWinPoints() {
        return Game.WIN_POINTS_SHOULD_BE_CALCULATED;
    }

    @Override
    public boolean isPausable() {
        return true;
    }

    @Override
    public String toString() {
        return "[Android Game]";
    }
}
