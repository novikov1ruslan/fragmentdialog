package com.ivygames.morskoiboi.screen;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;

import org.commons.logger.Ln;

public final class BackToSelectGameCommand implements Runnable {

    private final BattleshipActivity mBattleshipActivity;

    public BackToSelectGameCommand(BattleshipActivity activity) {
        mBattleshipActivity = activity;
    }

    @Override
    public void run() {
        Ln.d("returning to select game screen");
        Game game = Model.instance.game;
        if (game != null) {
            game.finish();
        }
        mBattleshipActivity.setScreen(new SelectGameScreen());
    }

}
