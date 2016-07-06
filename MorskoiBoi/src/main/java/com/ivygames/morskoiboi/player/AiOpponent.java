package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ivygames.morskoiboi.variant.RussianBot;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.Random;


public class AiOpponent extends PlayerOpponent {
    @NonNull
    private final Placement mPlacement;
    @NonNull
    private final Rules mRules;
    @NonNull
    private final BotAlgorithm mBot;

    @NonNull
    private DelegatePlayerCallback mCallback;

    public AiOpponent(@NonNull String name, @NonNull Placement placement, @NonNull Rules rules) {
        super(name, placement, rules);
        mPlacement = placement;
        mRules = rules;

        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME

        mCallback = new PlayerCallbackImpl();
        super.setCallback(mCallback);
        Ln.v("Android opponent created with bot: " + mBot);
    }

    @Override
    public void setCallback(@NonNull PlayerCallback callback) {
        mCallback.setCallback(callback);
        Ln.v(getName() + ": external callback set to " + callback);
    }

    @Override
    public void removeCallback() {
        mCallback.removeCallback();
    }

    private class PlayerCallbackImpl extends DelegatePlayerCallback {
        @Override
        public void opponentReady() {
            super.opponentReady();

            if (mMyBoard.getShips().isEmpty()) {
                Ln.v(getName() + ": opponent is waiting for me, I will place ships and start bidding...");
                placeShips();
                startBidding(new Bidder().newBid());
            }
        }

        @Override
        public void onPlayersTurn() {
            super.onPlayersTurn();

            mOpponent.onShotAt(mBot.shoot(mEnemyBoard));
        }
    }

    private void placeShips() {
        mPlacement.populateBoardWithShips(mMyBoard, generateFullFleet());
        if (BuildConfig.DEBUG) {
            Ln.i(this + ": my board: " + mMyBoard);
        }
    }

    @NonNull
    private Collection<Ship> generateFullFleet() {
        return GameUtils.generateShipsForSizes(mRules.getAllShipsSizes());
    }
}
