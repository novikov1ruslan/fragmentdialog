package com.ivygames.morskoiboi.ui;

import org.commons.logger.Ln;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;

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
		mBattleshipActivity.setScreen(new SelectGameFragment());
	}

}
