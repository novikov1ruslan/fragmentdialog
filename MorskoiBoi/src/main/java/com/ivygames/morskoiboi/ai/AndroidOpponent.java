package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.AbstractOpponent;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.acra.ACRA;
import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.util.Random;

public class AndroidOpponent extends AbstractOpponent {

    private volatile BotAlgorithm mAlgorithm;
    private final String mName;
    private Thread mThread;

    public AndroidOpponent(String name) {
        Validate.notNull(name);
        mName = name;
        reset();
        Ln.v("new android opponent created");
    }

    @Override
    protected void reset() {
        super.reset();
        mMyBoard = PlacementFactory.getAlgorithm().generateBoard();
        mAlgorithm = BotFactory.getAlgorithm();

        if (GameConstants.IS_TEST_MODE) {
            Ln.i(this + ": my board: " + mMyBoard);
        }
    }

    @Override
    public synchronized void go() {
        Vector2 aim = mAlgorithm.shoot(mEnemyBoard);
        boolean needThinking = mAlgorithm.needThinking(mEnemyBoard);
        needThinking = false;
        join();
        mThread = new Thread(new ShootAtOpponentCommand(mOpponent, aim, needThinking), "Bot");
        mThread.start();
    }

    synchronized void stopAi() {
        if (mThread == null) {
            Ln.v("AI not running");
        } else {
            Ln.d("stopping AI");
            mThread.interrupt();
            join();
        }
    }

    @Override
    public synchronized void onShotAt(Vector2 aim) {
        PokeResult result = createResultFor(aim);
        join();
        mThread = new Thread(new PassShotResultToOpponentCommand(mOpponent, result, mMyBoard), "bot");
        mThread.start();
    }

    @Override
    public void onShotResult(PokeResult result) {
        mAlgorithm.setLastResult(result);
        updateEnemyBoard(result);
        Ln.v(result);

        if (result.cell.isMiss()) {
            Ln.d(this + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
        } else if (result.ship != null) {
            if (Board.isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(this + ": I lost - notifying " + mOpponent);
                mOpponent.onLost(mMyBoard);
                reset();
            }
        }
    }

    @Override
    public String getName() {
        return mName;
    }

    private void join() {
        if (mThread != null && mThread.isAlive()) {
            try {
                Ln.w("need to join");
                mThread.interrupt();
                mThread.join();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public synchronized void bid(int bid) {
        Ln.v(this + ": enemy bid=" + bid);
        mEnemyBid = bid;
        join();
        if (mEnemyBid == mMyBid) {
            ACRA.getErrorReporter().handleException(new RuntimeException("stall"));
            mMyBid = new Random(System.currentTimeMillis() + this.hashCode()).nextInt(Integer.MAX_VALUE);
        }
        mThread = new Thread(new StartCommand(mOpponent, mMyBid, mEnemyBid), "bidding_boat");
        mThread.start();
    }

    @Override
    public void setOpponentVersion(int ver) {
        Ln.d("player's protocol version = " + ver);
    }

    @Override
    public void onLost(Board board) {
        Ln.d("android lost - preparing for the next round");
        join();
        reset();
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
