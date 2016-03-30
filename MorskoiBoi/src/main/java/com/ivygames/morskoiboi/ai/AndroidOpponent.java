package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.AbstractOpponent;
import com.ivygames.morskoiboi.Bidder;
import com.ivygames.morskoiboi.Cancellable;
import com.ivygames.morskoiboi.CancellableOpponent;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.variant.RussianBot;

import org.acra.ACRA;
import org.commons.logger.Ln;

import java.util.Random;

public class AndroidOpponent extends AbstractOpponent implements Cancellable {

    private volatile BotAlgorithm mBot;
    private final String mName;
    private final CancellableOpponent mDelegate;
    private final Rules mRules;
    protected Opponent mOpponent;

    public AndroidOpponent(@NonNull String name,
                           @NonNull PlacementAlgorithm placement,
                           @NonNull Rules rules,
                           @NonNull CancellableOpponent delegate) {
        super(placement);
        mName = name;
        mRules = rules;
        mDelegate = delegate;
        reset(new Bidder().newBid());
        Ln.v("new android opponent created");
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
        Ln.d(this + ": my opponent is " + opponent);
        mDelegate.setOpponent(opponent);
    }

    @Override
    protected final void reset(int myBid) {
        super.reset(myBid);
        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME

        placeShips();
    }

    private void placeShips() {
        mMyBoard = mPlacement.generateBoard();
        if (GameConstants.IS_TEST_MODE) {
            Ln.i(this + ": my board: " + mMyBoard);
        }
    }

    @Override
    public void go() {
        super.go();
        mDelegate.onShotAt(mBot.shoot(mEnemyBoard));
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = createResultForShootingAt(aim);
        mDelegate.onShotResult(result);
        boolean andGo = result.cell.isHit() && !mRules.isItDefeatedBoard(mMyBoard);
        if (andGo) {
            Ln.d("Android is hit, passing turn to " + mOpponent);
            mDelegate.go();
        }
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        mBot.setLastResult(result);
        updateEnemyBoard(result);
        Ln.v(result);

        if (result.cell.isMiss()) {
            Ln.d(this + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
        } else if (result.ship != null) {
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(this + ": I lost - notifying " + mOpponent);
                mOpponent.onLost(mMyBoard);
                reset(new Bidder().newBid());
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
        Ln.v(this + ": enemy bid=" + bid);
        mEnemyBid = bid;
        if (mEnemyBid == mMyBid) {
            ACRA.getErrorReporter().handleException(new RuntimeException("stall"));
            mMyBid = new Random(System.currentTimeMillis() + this.hashCode()).nextInt(Integer.MAX_VALUE);
        }
        mOpponent.setOpponentVersion(Opponent.CURRENT_VERSION);
        Ln.d("bidding against " + mOpponent + " with result " + isOpponentTurn());
        if (isOpponentTurn()) {
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
    public void onLost(Board board) {
        Ln.d("android lost - preparing for the next round");
        mDelegate.cancel();
        reset(new Bidder().newBid());
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        // mirroring
        mOpponent.onNewMessage(text + "!!!");
    }

    @Override
    public void init() {
        mDelegate.init();
    }

    @Override
    public void cancel() {
        mDelegate.cancel();
    }

    @Override
    public String toString() {
        return mName;
    }

}
