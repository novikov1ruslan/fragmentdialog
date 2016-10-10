package com.ivygames.morskoiboi.screen;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.BattleshipActivity;

import org.commons.logger.Ln;

public final class BackToSelectGameCommand implements Runnable {

    @NonNull
    private final BattleshipActivity mBattleshipActivity;

    public BackToSelectGameCommand(@NonNull BattleshipActivity activity) {
        mBattleshipActivity = activity;
    }

    @Override
    public void run() {
        Ln.d("returning to select game screen");
        mBattleshipActivity.setScreen(ScreenCreator.newSelectGameScreen());
    }

}
