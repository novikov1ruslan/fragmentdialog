package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.AbstractOpponent;
import com.ivygames.morskoiboi.Cancellable;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.variant.RussianBot;

import org.acra.ACRA;
import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.util.Random;

public class AndroidOpponent extends AbstractOpponent implements Cancellable {

    private volatile BotAlgorithm mBot;
    private final String mName;
    private final PlacementAlgorithm mPlacement;
    private DelayedOpponent mDelayedOpponent;

    public AndroidOpponent(@NonNull  String name, @NonNull PlacementAlgorithm placement) {
        mPlacement = placement;
        mName = Validate.notNull(name);
        reset(new Random());
        Ln.v("new android opponent created");
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        super.setOpponent(opponent);
        mDelayedOpponent = new DelayedOpponent(opponent, mMyBoard);
    }

    @Override
    protected void reset(@NonNull Random random) {
        super.reset(random);
        mMyBoard = mPlacement.generateBoard();
        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME

        if (GameConstants.IS_TEST_MODE) {
            Ln.i(this + ": my board: " + mMyBoard);
        }
    }

    @Override
    public void go() {
        mDelayedOpponent.onShotAt(mBot.shoot(mEnemyBoard));
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        mDelayedOpponent.onShotResult(createResultForShootingAt(aim));
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
            if (RulesFactory.getRules().isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(this + ": I lost - notifying " + mOpponent);
                mOpponent.onLost(mMyBoard);
                reset(new Random());
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
        Ln.v(this + ": enemy bid=" + bid);
        mEnemyBid = bid;
        if (mEnemyBid == mMyBid) {
            ACRA.getErrorReporter().handleException(new RuntimeException("stall"));
            mMyBid = new Random(System.currentTimeMillis() + this.hashCode()).nextInt(Integer.MAX_VALUE);
        }
        mOpponent.setOpponentVersion(Opponent.CURRENT_VERSION);
        Ln.d("bidding against " + mOpponent + " with result " + isOpponentTurn());
        if (isOpponentTurn()) {
            mDelayedOpponent.go();
        } else {
            mDelayedOpponent.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void setOpponentVersion(int ver) {
        Ln.d("player's protocol version = " + ver);
    }

    @Override
    public void onLost(Board board) {
        Ln.d("android lost - preparing for the next round");
        mDelayedOpponent.join();
        reset(new Random());
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        // mirroring
        mOpponent.onNewMessage(text + "!!!");
    }

    @Override
    public void cancel() {
        mDelayedOpponent.cancel();
    }

    @Override
    public String toString() {
        return mName;
    }

}
