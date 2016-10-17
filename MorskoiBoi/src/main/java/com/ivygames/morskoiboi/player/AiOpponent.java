package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.ai.Cancellable;
import com.ivygames.morskoiboi.variant.RussianBot;

import org.commons.logger.Ln;

import java.util.Random;


public class AiOpponent extends PlayerOpponent implements Cancellable {
    @NonNull
    private final Placement mPlacement;
    @NonNull
    private final Rules mRules;
    @NonNull
    private final BotAlgorithm mBot;

    public AiOpponent(@NonNull String name, @NonNull Placement placement, @NonNull Rules rules) {
        super(name, placement, rules);
        mPlacement = placement;
        mRules = rules;

        mBot = new RussianBot(new Random(System.currentTimeMillis())); // TODO: generalize FIXME

        registerCallback(new PlayerCallbackImpl());
        Ln.v("Android opponent created with bot: " + mBot);
    }

    @Override
    public void cancel() {
        if (mOpponent instanceof Cancellable) {
            Ln.v(mOpponent + " is cancellable, cancelling");
            ((Cancellable) mOpponent).cancel();
        }
    }

    private class PlayerCallbackImpl extends DummyCallback {
        @Override
        public void opponentReady() {
            if (getBoard().getShips().isEmpty()) {
                Ln.v(getName() + ": opponent is waiting for me, I will place ships and start bidding...");
                placeShips();
            }

            if (!ready()) {
                startBidding(new Bidder().newBid());
            }
        }

        @Override
        public void onPlayersTurn() {
            mOpponent.onShotAt(mBot.shoot(getEnemyBoard()));
        }
    }

    @VisibleForTesting
    protected final void placeShips() {
        mPlacement.populateBoardWithShips(getBoard(), mRules.generateFullFleet());
        if (BuildConfig.DEBUG) {
            Ln.i(getName() + ": my board: " + getBoard());
        }
    }

}
