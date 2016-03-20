package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Opponent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

class OnEnemyBidCommand implements Runnable {
    private static final int START_TIMEOUT = 3000;

    private final Opponent mOpponent;

    private final int mMyBid;

    OnEnemyBidCommand(Opponent opponent, int myBid) {
        mOpponent = Validate.notNull(opponent);
        mMyBid = myBid;
    }

    @Override
    public void run() {
        Ln.v("begin");
        try {
            Thread.sleep(START_TIMEOUT);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            Ln.d("game ended - stopping bidding");
            return;
        }

        mOpponent.onEnemyBid(mMyBid);
        Ln.v("end");
    }

}
