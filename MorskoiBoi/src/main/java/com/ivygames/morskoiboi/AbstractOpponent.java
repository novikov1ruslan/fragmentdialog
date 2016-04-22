package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public abstract class AbstractOpponent implements Opponent {
    protected static final int OPPONENT_NOT_READY_BID = -2;
    private static final int OPPONENT_READY_BID = -1;

    @NonNull
    protected Board mMyBoard = new Board();
    @NonNull
    protected Board mEnemyBoard = new Board();

    protected volatile int mMyBid;
    protected volatile int mEnemyBid;

    protected void reset(int myBid) {
        Ln.d(this + ": initializing boards and bids");
        mEnemyBoard.clearBoard();
        mMyBoard.clearBoard();
        mMyBid = myBid;
        mEnemyBid = OPPONENT_NOT_READY_BID;
    }

    @Override
    public void go() {
        if (!isOpponentReady()) {
            Ln.d(this + ": opponent is ready");
            mEnemyBid = OPPONENT_READY_BID;
        }
    }

    public boolean isOpponentReady() {
        return mEnemyBid != OPPONENT_NOT_READY_BID;
    }

    @Override
    public void onEnemyBid(int bid) {
        mEnemyBid = bid;
        Ln.d(this + ": opponent is ready, bid = " + bid);
    }

    /**
     * marks the aimed cell
     */
    protected final PokeResult createResultForShootingAt(Vector2 aim) {
        // ship if found will be shot and returned
        Ship ship = mMyBoard.getFirstShipAt(aim);

        // this cell will be changed and returned in result later
        Cell cell = mMyBoard.getCellAt(aim);

        if (ship == null) {
            cell.setMiss();
        } else {
            cell.setHit();
            ship.shoot();

            if (ship.isDead()) {
                return new PokeResult(aim, cell, ship);
            }
        }

        return new PokeResult(aim, cell);
    }

    protected final void updateEnemyBoard(@NonNull PokeResult result,
                                          @NonNull PlacementAlgorithm placement) {
        Ship ship = result.ship;
        if (ship == null) {
            mEnemyBoard.setCell(result.cell, result.aim);
        } else {
            placement.putShipAt(mEnemyBoard, ship, ship.getX(), ship.getY());
        }
        Ln.v(mEnemyBoard);
    }

    public final boolean isOpponentTurn() {
        return mMyBid < mEnemyBid;
    }

}
