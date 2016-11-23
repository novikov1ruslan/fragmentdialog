package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coord;
import com.ivygames.battleship.board.LocatedShip;
import com.ivygames.battleship.ship.Ship;

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

    public void dropShip(@NonNull Board board, @NonNull Coord coordinate) {
        dropShip(board, coordinate.i, coordinate.j);
    }

    public void dropShip(@NonNull Board board, int i, int j) {
        if (mPickedShip == null) {
            return;
        }

        if (BoardUtils.shipFitsTheBoard(mPickedShip, i, j)) {
            board.addShip(new LocatedShip(mPickedShip, i, j));
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
    public Ship pickShipFromBoard(@NonNull Board board, @NonNull Coord v) {
        return pickShipFromBoard(board, v.i, v.j);
    }

    @Nullable
    public Ship pickShipFromBoard(@NonNull Board board, int i, int j) {
        mPickedShip = BoardUtils.pickShipFromBoard(board, i, j);
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
