package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
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

    @NonNull
    private DelegatePlayerCallback mCallback;
    private Cancellable mCancellable;

    public AiOpponent(@NonNull String name, @NonNull Placement placement, @NonNull Rules rules) {
        super(name, placement, rules);
        mPlacement = placement;
        mRules = rules;

        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME

        mCallback = new PlayerCallbackImpl();
        super.registerCallback(mCallback);
        Ln.v("Android opponent created with bot: " + mBot);
    }

    @Override
    public void registerCallback(@NonNull PlayerCallback callback) {
        mCallback.setCallback(callback);
        Ln.v(getName() + ": external callback set to " + callback);
    }

    @Override
    public void clearCallbacks() {
        mCallback.removeCallback();
    }

    public void setCancellable(@NonNull Cancellable cancellable) {
        mCancellable = cancellable;
        Ln.v(getName() + ": cancellable set to: " + cancellable);
    }

    @Override
    public void cancel() {
        mCancellable.cancel();
    }

    private class PlayerCallbackImpl extends DelegatePlayerCallback {
        @Override
        public void opponentReady() {
            super.opponentReady();

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
            super.onPlayersTurn();

            mOpponent.onShotAt(mBot.shoot(getEnemyBoard()));
        }
    }

    private void placeShips() {
        mPlacement.populateBoardWithShips(getBoard(), mRules.generateFullFleet());
        if (BuildConfig.DEBUG) {
            Ln.i(getName() + ": my board: " + getBoard());
        }
    }

}
