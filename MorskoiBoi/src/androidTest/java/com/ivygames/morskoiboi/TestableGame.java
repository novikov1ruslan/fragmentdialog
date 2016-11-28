package com.ivygames.morskoiboi;

public class TestableGame extends Game {

    private final Game mGame;
    private boolean mHasFinished;

    public TestableGame(Game game) {
        mGame = game;
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
    public boolean isRemote() {
        return mGame.isRemote();
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

    @Override
    public boolean isPausable() {
        return mGame.isPausable();
    }

    public boolean hasFinished() {
        return mHasFinished;
    }

    @Override
    public String toString() {
        return "[T[" + mGame + "]]";
    }
}
