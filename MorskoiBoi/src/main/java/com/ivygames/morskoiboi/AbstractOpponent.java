package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public abstract class AbstractOpponent implements Opponent {
    private static final int IMPOSSIBLE_BID = -1;

    protected Board mMyBoard;
    protected Board mEnemyBoard;

    protected volatile int mMyBid;
    protected volatile int mEnemyBid;

    protected final PlacementAlgorithm mPlacement;

    private boolean mOpponentReady;

    protected AbstractOpponent(PlacementAlgorithm placement) {
        mPlacement = placement;
    }

    protected void reset(int myBid) {
        Ln.d(this + ": initializing boards and bids");
        mEnemyBoard = new Board();
        mMyBid = myBid;
        mEnemyBid = IMPOSSIBLE_BID;
        mOpponentReady = false;
    }

    @Override
    public void go() {
        if (!mOpponentReady) {
            Ln.d(this + ": opponent is ready");
            mOpponentReady = true;
        }
    }

    public boolean isOpponentReady() {
        return mOpponentReady;
    }

    @Override
    public void onEnemyBid(int bid) {
        mOpponentReady = true;
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

    protected final void updateEnemyBoard(PokeResult result) {
        Ship ship = result.ship;
        if (ship == null) {
            mEnemyBoard.setCell(result.cell, result.aim);
        } else {
            mPlacement.putShipAt(mEnemyBoard, ship, ship.getX(), ship.getY());
        }
        Ln.v(mEnemyBoard);
    }

    public final boolean isOpponentTurn() {
        return mMyBid < mEnemyBid;
    }

}
