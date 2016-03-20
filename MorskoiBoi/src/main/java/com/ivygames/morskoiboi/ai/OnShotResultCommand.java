package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

final class OnShotResultCommand implements Runnable {
    private static final int WHISTLE_SOUND_DELAY = 1300;
    private final Opponent mOpponent;
    private final PokeResult mResult;

    OnShotResultCommand(Opponent opponent, PokeResult result) {
        mOpponent = Validate.notNull(opponent);
        mResult = Validate.notNull(result);
    }

    @Override
    public void run() {
        Ln.v("begin");
        try {
            Thread.sleep(WHISTLE_SOUND_DELAY);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            Ln.d("game ended - stopping shell standby");
            return;
        }

        mOpponent.onShotResult(mResult);
        Ln.v("end");
    }

}
