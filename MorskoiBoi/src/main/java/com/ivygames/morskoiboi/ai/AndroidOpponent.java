package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.AbstractOpponent;
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

public class AndroidOpponent extends AbstractOpponent {

    private volatile BotAlgorithm mBot;
    private final String mName;
    private PlacementAlgorithm mPlacement;
    private DelayedOpponent mDelayedOpponent;

    public AndroidOpponent(String name, PlacementAlgorithm placementAlgorithm) {
        mPlacement = placementAlgorithm;
        mName = Validate.notNull(name);
        reset(new Random());
        Ln.v("new android opponent created");
    }

    @Override
    public void setOpponent(Opponent opponent) {
        super.setOpponent(opponent);
        mDelayedOpponent = new DelayedOpponent(opponent, mMyBoard);
    }

    @Override
    protected void reset(Random random) {
        super.reset(random);
        mMyBoard = mPlacement.generateBoard();
        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME

        if (GameConstants.IS_TEST_MODE) {
            Ln.i(this + ": my board: " + mMyBoard);
        }
    }

    @Override
    public synchronized void go() {
        Vector2 aim = mBot.shoot(mEnemyBoard);
        mDelayedOpponent.onShotAt(aim);
    }

    synchronized void stopAi() {
        mDelayedOpponent.stopAi();
    }

    @Override
    public synchronized void onShotAt(Vector2 aim) {
        mDelayedOpponent.onShotResult(createResultForShootingAt(aim));
    }

    @Override
    public void onShotResult(PokeResult result) {
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

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public synchronized void onEnemyBid(int bid) {
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
    public void onNewMessage(String text) {
        // mirroring
        mOpponent.onNewMessage(text + "!!!");
    }

    @Override
    public String toString() {
        return mName;
    }
}
