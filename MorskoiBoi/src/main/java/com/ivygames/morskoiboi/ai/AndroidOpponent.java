package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.AbstractOpponent;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ivygames.morskoiboi.variant.RussianBot;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.Random;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class AndroidOpponent extends AbstractOpponent implements Cancellable {

    @NonNull
    private BotAlgorithm mBot;
    @NonNull
    private final String mName;
    private Cancellable mCancellable;
    @NonNull
    private final Placement mPlacement;
    @NonNull
    private final Opponent mDelegate;
    @NonNull
    private final Rules mRules;
//    private Opponent mOpponent;

    public AndroidOpponent(@NonNull String name,
                           @NonNull Board board,
                           @NonNull Placement placement,
                           @NonNull Rules rules,
                           @NonNull Opponent delegate) {
        mName = name;
        mMyBoard = board;
        mPlacement = placement;
        mRules = rules;
        mDelegate = delegate;

        Ln.d(this + ": initializing boards and bids");
        mMyBid = new Bidder().newBid();
        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME
        Ln.v("new android opponent created");
    }

    private void reset2() {
        super.reset();
        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME
        mMyBid = new Bidder().newBid();
    }

    public void setCancellable (@NonNull Cancellable cancellable) {
        mCancellable = cancellable;
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        Ln.d(this + ": my opponent is " + opponent);
        mDelegate.setOpponent(opponent);
        mDelegate.setOpponentVersion(Opponent.CURRENT_VERSION);
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

    @Override
    public void go() {
        if (!isOpponentReady()) {
            placeShips();
        }
        super.go();
        mDelegate.onShotAt(mBot.shoot(mEnemyBoard));
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = createResultForShootingAt(aim);
        mDelegate.onShotResult(result);
        boolean andGo = result.cell.isHit() && !mRules.isItDefeatedBoard(mMyBoard);
        if (andGo) {
            Ln.d("Android is hit, passing turn to " + mDelegate);
            mDelegate.go();
        }
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        mBot.setLastResult(result);
        updateEnemyBoard(result, mPlacement);
        Ln.v(result);

        if (result.cell.isMiss()) {
            Ln.d(this + ": I missed - passing the turn to " + mDelegate);
            mDelegate.go();
        } else if (result.ship != null) {
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(this + ": I won - notifying " + mDelegate);
                mDelegate.onLost(Board.copy(mMyBoard));
                reset2();
            }
        }
    }

    @NonNull
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void onEnemyBid(int bid) {
        super.onEnemyBid(bid);
        placeShips();
        if (mEnemyBid == mMyBid) {
            reportException("stall");
            mMyBid = new Random(System.currentTimeMillis() + hashCode()).nextInt(Integer.MAX_VALUE);
        }
        Ln.d("bidding against " + mDelegate + " with result " + opponentStarts());
        if (opponentStarts()) {
            mDelegate.go();
        } else {
            mDelegate.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void setOpponentVersion(int ver) {
        Ln.d("player's protocol version = " + ver);
    }

    @Override
    public void onLost(@NonNull Board board) {
        Ln.d("android lost - preparing for the next round");
        if (mCancellable != null) {
            mCancellable.cancel();
        }
        reset2();
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        // mirroring
        mDelegate.onNewMessage(text + "!!!");
    }

    @Override
    public void cancel() {
        if (mCancellable != null) {
            mCancellable.cancel();
        }
    }

    @Override
    public String toString() {
        return mName;
    }

}
