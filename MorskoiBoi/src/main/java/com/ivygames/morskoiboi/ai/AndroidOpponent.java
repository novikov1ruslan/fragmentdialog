package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.AbstractOpponent;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.BoardSerialization;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ivygames.morskoiboi.variant.RussianBot;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.Random;

public class AndroidOpponent extends AbstractOpponent implements Cancellable {

    private Cancellable mCancellable;
    @NonNull
    private final Placement mPlacement;
    private Opponent mOpponent;
    @NonNull
    private final Rules mRules;

    @NonNull
    private final BotAlgorithm mBot;

    public AndroidOpponent(@NonNull String name,
                           @NonNull Board board,
                           @NonNull Placement placement,
                           @NonNull Rules rules) {
        super(name);
        mMyBoard = board;
        mPlacement = placement;
        mRules = rules;

        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME
        mMyBid = new Bidder().newBid();
        placeShips();
        Ln.v("new android opponent created");
    }

    private void reset2() {
        super.reset();
        placeShips();
        if (mCancellable != null) {
            mCancellable.cancel();
        }
    }

    public void setCancellable (@NonNull Cancellable cancellable) {
        mCancellable = cancellable;
    }

    @Override
    public void cancel() {
        if (mCancellable != null) {
            mCancellable.cancel();
        }
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        Ln.d(this + ": my opponent is " + opponent);
        mOpponent = opponent;
        mOpponent.setOpponentVersion(Opponent.CURRENT_VERSION);
    }

    @Override
    public void go() {
        if (!isOpponentReady()) {
            placeShips();
        }
        super.go();
        mOpponent.onShotAt(mBot.shoot(mEnemyBoard));
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = createResultForShootingAt(aim);
        mOpponent.onShotResult(result);
        boolean andGo = result.cell.isHit() && !mRules.isItDefeatedBoard(mMyBoard);
        if (andGo) {
            Ln.d("Android is hit, passing turn to " + mOpponent);
            mOpponent.go();
        }
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        updateEnemyBoard(result, mPlacement);
        Ln.v(result);

        if (result.cell.isMiss()) {
            Ln.d(this + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
        } else if (result.ship != null) {
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(this + ": I won - notifying " + mOpponent);
                mOpponent.onLost(BoardSerialization.copy(mMyBoard));
                reset2();
            }
        }
    }

    @Override
    public void onEnemyBid(int bid) {
        super.onEnemyBid(bid);

        Ln.d("bidding against " + mOpponent + " with result " + opponentStarts());
        if (opponentStarts()) {
            mOpponent.go();
        } else {
            mOpponent.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void setOpponentVersion(int ver) {
        Ln.d("player's protocol version = " + ver);
    }

    @Override
    public void onLost(@NonNull Board board) {
        Ln.d("android lost - preparing for the next round");
        reset2();
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        // mirroring
        mOpponent.onNewMessage(text + "!!!");
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
