package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ShipUtils;
import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.ai.Cancellable;
import com.ivygames.morskoiboi.model.Ship;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.Random;


public class AiOpponent extends PlayerOpponent implements Cancellable {
    @NonNull
    private final Placement mPlacement;
    @NonNull
    private final Rules mRules;
    @NonNull
    private final BotAlgorithm mBot;
    @NonNull
    private final Bidder mBidder;
    @NonNull
    private final Random mRandom;

    public AiOpponent(@NonNull String name, @NonNull Placement placement,
                      @NonNull Rules rules, @NonNull BotAlgorithm bot,
                      @NonNull Bidder bidder, @NonNull Random random) {
        super(name, placement, rules);
        mPlacement = placement;
        mRules = rules;
        mBot = bot;
        mBidder = bidder;
        mRandom = random;

        registerCallback(new PlayerCallbackImpl());
        Ln.v("Android opponent created with bot: " + bot);
    }

    @Override
    public void cancel() {
        if (mOpponent instanceof Cancellable) {
            Ln.v(this + ": cancelling " + mOpponent);
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
                Ln.d(AiOpponent.this + ": opponent is ready, but I'm not, start bidding");
                startBidding(mBidder.newBid());
            }
        }

        @Override
        public void onPlayersTurn() {
            mOpponent.onShotAt(mBot.shoot(getEnemyBoard()));
        }
    }

    private void placeShips() {
        ShipUtils.OrientationBuilder orientationBuilder = new ShipUtils.OrientationBuilder(mRandom);
        Collection<Ship> ships = ShipUtils.generateFullFleet(mRules.getAllShipsSizes(), orientationBuilder);
        mPlacement.populateBoardWithShips(getBoard(), ships);
        if (BuildConfig.DEBUG) {
            Ln.i(getName() + ": my board: " + getBoard());
        }
    }

}
