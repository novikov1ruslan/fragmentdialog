package com.ivygames.morskoiboi.screen;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Game;

public class EndGameCommand implements Runnable {
    @NonNull
    private final Game mGame;
    @NonNull
    private final Runnable mDelegate;

    public EndGameCommand(@NonNull Game game, @NonNull Runnable delegate) {
        mGame = game;
        mDelegate = delegate;
    }

    @Override
    public void run() {
        mGame.finish();
        mDelegate.run();
    }

    @Override
    public String toString() {
        return "[" + EndGameCommand.class.getSimpleName() + "[" + mDelegate + "]]";
    }
}
