package com.ivygames.morskoiboi.ui;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;

import org.commons.logger.Ln;

class BackToSelectGameCommand implements Runnable {

	private final BattleshipActivity mBattleshipActivity;

	BackToSelectGameCommand(BattleshipActivity activity) {
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
