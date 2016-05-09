package com.ivygames.morskoiboi.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.GameHandler;
import com.ivygames.morskoiboi.model.Game;

import org.commons.logger.Ln;

public final class BackToSelectGameCommand implements Runnable {

    @NonNull
    private final BattleshipActivity mBattleshipActivity;
    @Nullable
    private final Game mGame;

    public BackToSelectGameCommand(@NonNull BattleshipActivity activity, @Nullable Game game) {
        mBattleshipActivity = activity;
        mGame = game;
    }

    @Override
    public void run() {
        Ln.d("returning to select game screen");
        if (mGame != null && !mGame.hasFinished()) {
            mGame.finish();
        }
        mBattleshipActivity.setScreen(GameHandler.newSelectGameScreen());
    }

}
