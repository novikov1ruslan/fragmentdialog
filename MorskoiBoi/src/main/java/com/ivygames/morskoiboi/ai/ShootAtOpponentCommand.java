package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.Vector2;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

final class ShootAtOpponentCommand implements Runnable {
    private final Opponent mOpponent;
    private final boolean mNeedThinking;
    private final Vector2 mAim;

    ShootAtOpponentCommand(Opponent opponent, Vector2 aim, boolean needThinking) {
        mOpponent = Validate.notNull(opponent);
        mAim = Validate.notNull(aim);
        mNeedThinking = needThinking;
    }

    @Override
    public void run() {
        Ln.v("begin");
        ShootAtOpponentCommand.simulateThinking(mNeedThinking);
        if (Thread.currentThread().isInterrupted()) {
            Ln.d("game ended - stopping thinking process");
            return;
        }

        mOpponent.onShotAt(mAim);
        Ln.v("end");
    }

    private static void simulateThinking(boolean needThinking) {
        // wait from 1 to 2.5 seconds
        int extraTime = needThinking ? 1000 : 0;
        long millis = 1000 + (int) (Math.random() * (500 + extraTime));
        ShootAtOpponentCommand.sleep(millis);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

}
