package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Game;

public class TestableGame extends Game {

    private final Game mGame;
    private boolean mHasFinished;

    public TestableGame(Game game) {
        mGame = game;
    }

    @Override
    public Type getType() {
        return mGame.getType();
    }

    @Override
    public boolean shouldNotifyOpponent() {
        return mGame.shouldNotifyOpponent();
    }

    @Override
    public int getTurnTimeout() {
        return mGame.getTurnTimeout();
    }

    @Override
    public boolean hasSetupTimeout() {
        return mGame.hasSetupTimeout();
    }

    @Override
    public boolean supportsAchievements() {
        return mGame.supportsAchievements();
    }

    @Override
    public int getWinPoints() {
        return mGame.getWinPoints();
    }

    @Override
    public boolean finish() {
        mHasFinished = true;
        return mGame.finish();
    }

    public boolean hasFinished() {
        return mHasFinished;
    }
}
