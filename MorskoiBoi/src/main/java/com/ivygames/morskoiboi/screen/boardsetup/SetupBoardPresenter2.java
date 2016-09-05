package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

import java.util.PriorityQueue;

public class SetupBoardPresenter2 {

    @NonNull
    private final Placement mPlacement = PlacementFactory.getAlgorithm();

    /**
     * currently picked ship (awaiting to be placed)
     */
    private Ship mPickedShip;

    private PriorityQueue<Ship> mDockedShips;

    /**
     * ship displayed at the top of the screen (selection area)
     */
    private Ship mDockedShip;

    public Ship getPickedShip() {
        return mPickedShip;
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

    public Ship getDockedShip() {
        return mDockedShip;
    }

    public void setFleet(@NonNull PriorityQueue<Ship> ships) {
        mDockedShips = ships;
        setDockedShip();
    }

    public void rotateShipAt(@NonNull Board board, @NonNull Vector2 aim) {
        rotateShipAt(board, aim.getX(), aim.getY());
    }

    public void rotateShipAt(@NonNull Board board, int i, int j) {
        mPlacement.rotateShipAt(board, i, j);
    }

    public void dropShip(@NonNull Board board, @NonNull Vector2 coordinate) {
        dropShip(board, coordinate.getX(), coordinate.getY());
    }

    public void dropShip(@NonNull Board board, int i, int j) {
        if (mPickedShip == null) {
            return;
        }

        if (board.shipFitsTheBoard(mPickedShip, i, j)) {
            mPlacement.putShipAt(board, mPickedShip, i, j);
        } else {
            returnShipToPool(mPickedShip);
        }

        setDockedShip();
        mPickedShip = null;
    }

    public void pickShipFromBoard(@NonNull Board board, @NonNull Vector2 coordinate) {
        pickShipFromBoard(board, coordinate.getX(), coordinate.getY());
    }

    public void pickShipFromBoard(@NonNull Board board, int i, int j) {
        mPickedShip = mPlacement.removeShipFrom(board, i, j);
    }
}
