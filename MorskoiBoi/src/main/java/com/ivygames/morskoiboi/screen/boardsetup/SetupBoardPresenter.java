package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

import java.util.PriorityQueue;

public class SetupBoardPresenter {

    /**
     * ship displayed at the top of the screen (selection area)
     */
    private Ship mDockedShip;

    private PriorityQueue<Ship> mDockedShips;

    /**
     * currently picked ship (awaiting to be placed)
     */
    @Nullable
    private Ship mPickedShip;

    public Ship getDockedShip() {
        return mDockedShip;
    }

    public boolean hasPickedShip() {
        return mPickedShip != null;
    }

    public void pickDockedShip() {
        mPickedShip = mDockedShips.poll();
        if (mPickedShip == null) {
            Ln.v("no ships to pick");
        } else {
            mDockedShip = null;
            Ln.v(mPickedShip + " picked from stack, stack: " + mDockedShips);
        }
    }

    public void dropShip(@NonNull Board board, @NonNull Vector2 coordinate) {
        dropShip(board, coordinate.x, coordinate.y);
    }

    public void dropShip(@NonNull Board board, int i, int j) {
        if (mPickedShip == null) {
            return;
        }

        if (board.shipFitsTheBoard(mPickedShip, i, j)) {
            Placement.putShipAt(board, new Board.LocatedShip(mPickedShip, i, j));
        } else {
            returnShipToPool(mPickedShip);
        }

        setDockedShip();
        mPickedShip = null;
    }

    private void returnShipToPool(@NonNull Ship ship) {
        if (!ship.isHorizontal()) {
            ship.rotate();
        }
        mDockedShips.add(ship);
    }

    public void setDockedShip() {
        if (mDockedShips.isEmpty()) {
            mDockedShip = null;
        } else {
            mDockedShip = mDockedShips.peek();
        }
    }

    @Nullable
    public Ship pickShipFromBoard(@NonNull Board board, @NonNull Vector2 v) {
        return pickShipFromBoard(board, v.x, v.y);
    }

    @Nullable
    public Ship pickShipFromBoard(@NonNull Board board, int i, int j) {
        mPickedShip = Placement.pickShipFromBoard(board, i, j);
        return mPickedShip;
    }

    public void setFleet(@NonNull PriorityQueue<Ship> ships) {
        mDockedShips = ships;
        setDockedShip();
    }

    @Nullable
    public Ship getPickedShip() {
        return mPickedShip;
    }
}
