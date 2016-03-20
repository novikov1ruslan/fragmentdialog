package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Opponent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

class GoCommand implements Runnable {
    private static final int START_TIMEOUT = 3000;

    private final Opponent mOpponent;
    private final boolean mShouldWait;

    GoCommand(Opponent opponent, boolean shouldWait) {
        mOpponent = Validate.notNull(opponent);
        mShouldWait = shouldWait;
    }

    @Override
    public void run() {
        Ln.v("begin");
        if (mShouldWait) {
            try {
                Thread.sleep(START_TIMEOUT);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                Ln.d("game ended - stopping bidding");
                return;
            }
        }

        mOpponent.go();
        Ln.v("end");
    }

}
