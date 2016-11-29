package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.OrientationBuilder;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.battleship.Rules;
import com.ivygames.morskoiboi.ai.Cancellable;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.Random;


public class AiOpponent extends PlayerOpponent implements Cancellable {
    @NonNull
    private final Rules mRules;
    @NonNull
    private final Bot mBot;
    @NonNull
    private final Bidder mBidder;
    @NonNull
    private final Random mRandom;
    private boolean mOpponentReady;

    public AiOpponent(@NonNull String name,
                      @NonNull Rules rules, @NonNull Bot bot,
                      @NonNull Bidder bidder, @NonNull Random random) {
        super(name, rules.getAllShipsSizes().length);
        mRules = rules;
        mBot = bot;
        mBidder = bidder;
        mRandom = random;
        Ln.v("Android opponent created with bot: " + bot);
    }

    @Override
    public void reset() {
        super.reset();
        mOpponentReady = false;
    }

    @Override
    public void cancel() {
        if (mOpponent instanceof Cancellable) {
            Ln.v(this + ": cancelling " + mOpponent);
            ((Cancellable) mOpponent).cancel();
        }
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        super.setOpponent(opponent);
    }

    private void start() {
        if (mOpponentReady) {
            // TODO: unit test to test lack of the return
            return;
        }

        mOpponentReady = true;

        if (getBoard().getShips().isEmpty()) {
            Ln.v(getName() + ": opponent is waiting for me, I will place ships and start bidding...");
            placeShips();
        }

        if (!ready()) {
            Ln.d(getName() + ": opponent is ready, but I'm not, start bidding");
            startBidding(mBidder.newBid());
        }
    }

    @Override
    public void go() {
        super.go();
        start();
        mOpponent.onShotAt(mBot.shoot(getEnemyBoard()));
    }

    @Override
    public void onEnemyBid(int bid) {
        super.onEnemyBid(bid);
        start();
    }

    private void placeShips() {
        Placement placement = new Placement(mRandom, mRules.allowAdjacentShips());
        OrientationBuilder orientationBuilder = new OrientationBuilder(mRandom);
        Collection<Ship> ships = ShipUtils.generateFullFleet(mRules.getAllShipsSizes(), orientationBuilder);
        Board board = new Board();
        placement.populateBoardWithShips(board, ships);
        setBoard(board);
        if (BuildConfig.DEBUG) {
            Ln.i(getName() + ": my board: " + getBoard());
        }
    }

}
