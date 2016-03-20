package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

final class PassShotResultToOpponentCommand implements Runnable {
    private static final int WHISTLE_SOUND_DELAY = 1300;
    private final Opponent mOpponent;
    private final PokeResult mResult;
    private final Board mMyBoard;

    PassShotResultToOpponentCommand(Opponent opponent, PokeResult result, Board board) {
        mOpponent = Validate.notNull(opponent);
        mResult = Validate.notNull(result);
        mMyBoard = Validate.notNull(board);
    }

    @Override
    public void run() {
        // FIXME: data accessed in this method is not synchronized - not volatile
        Ln.v("begin");
        try {
            Thread.sleep(WHISTLE_SOUND_DELAY);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            Ln.d("game ended - stopping shell standby");
            return;
        }

        mOpponent.onShotResult(mResult);
        if (mResult.cell.isHit() && !RulesFactory.getRules().isItDefeatedBoard(mMyBoard)) {
            Ln.d("Android is hit, passing turn to " + mOpponent);
            mOpponent.go();
        }
        Ln.v("end");
    }

}
