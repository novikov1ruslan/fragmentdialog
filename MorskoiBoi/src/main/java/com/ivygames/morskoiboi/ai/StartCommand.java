package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Opponent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

class StartCommand implements Runnable {
	private static final int START_TIMEOUT = 3000;

	private final Opponent mOpponent;

	private final int mMyBid;

	private final int mOpponentBid;

	StartCommand(Opponent opponent, int myBid, int opponentBid) {
		mOpponent = Validate.notNull(opponent);
		mMyBid = myBid;
		mOpponentBid = opponentBid;
	}

	@Override
	public void run() {
		Ln.v("begin");
		Ln.d("bidding against " + mOpponent + " with result " + isOpponentTurn());
		try {
			Thread.sleep(START_TIMEOUT);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			Ln.d("game ended - stopping bidding");
			return;
		}

		mOpponent.setOpponentVersion(Opponent.CURRENT_VERSION);
		if (isOpponentTurn()) {
			mOpponent.go();
		} else {
			mOpponent.bid(mMyBid);
		}
		Ln.v("end");
	}

	private boolean isOpponentTurn() {
		return mMyBid < mOpponentBid;
	}

}
