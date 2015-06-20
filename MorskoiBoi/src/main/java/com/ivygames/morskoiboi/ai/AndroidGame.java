package com.ivygames.morskoiboi.ai;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import com.ivygames.morskoiboi.model.Game;

public class AndroidGame extends Game {
	private static final int TURN_TIMEOUT = 2 * 60 * 1000;

	private final AndroidOpponent mOpponent;

	public AndroidGame(AndroidOpponent opponent) {
		super();

		Validate.notNull(opponent);
		mOpponent = opponent;
		Ln.v("new android game created");
	}

	@Override
	public void finish() {
		if (hasFinished()) {
			Ln.w(getType() + " already finished");
			return;
		}

		super.finish();
		Ln.d("finishing Android game - AI stopped");
	}

	@Override
	public void finishMatch() {
		Ln.d("finishing match");
		super.finishMatch();
		mOpponent.stopAi();
	}

	@Override
	public Type getType() {
		return Type.VS_ANDROID;
	}

	@Override
	public int getTurnTimeout() {
		return TURN_TIMEOUT;
	}

}
