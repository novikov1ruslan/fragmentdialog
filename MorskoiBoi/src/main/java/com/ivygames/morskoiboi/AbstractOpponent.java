package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public abstract class AbstractOpponent implements Opponent {
    private static final int NOT_READY = -1;

    @NonNull
    protected Board mMyBoard = new Board();
    @NonNull
    protected Board mEnemyBoard = new Board();

    protected int mMyBid = NOT_READY;
    protected int mEnemyBid = NOT_READY;

    private boolean mOpponentReady;

    protected final void reset() {
        Ln.d(this + ": initializing boards and bids");
        mEnemyBoard = new Board();
        mMyBoard = new Board();
        mMyBid = NOT_READY;
        mEnemyBid = NOT_READY;
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
        mEnemyBid = bid;
        mOpponentReady = true;
        Ln.d(this + ": opponent is ready, bid = " + bid);
    }

    /**
     * marks the aimed cell
     */
    protected final PokeResult createResultForShootingAt(@NonNull Vector2 aim) {
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
                                          @NonNull Placement placement) {
        Ship ship = result.ship;
        if (ship == null) {
            mEnemyBoard.setCell(result.cell, result.aim);
        } else {
            placement.putShipAt(mEnemyBoard, ship, ship.getX(), ship.getY());
        }
        Ln.v(this + ": opponent's board: " + mEnemyBoard);
    }

    protected final boolean opponentStarts() {
        return mMyBid < mEnemyBid;
    }

    public void startBidding(int bid) {
        mMyBid = bid;
    }

}
