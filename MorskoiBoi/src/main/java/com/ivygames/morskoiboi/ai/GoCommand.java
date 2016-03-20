package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Opponent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

class GoCommand implements Runnable {
    private static final int START_TIMEOUT = 3000;

    private final Opponent mOpponent;

    GoCommand(Opponent opponent) {
        mOpponent = Validate.notNull(opponent);
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

        mOpponent.go();
        Ln.v("end");
    }

}
