package com.ivygames.battleship;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.Ship;

import java.util.Collection;
import java.util.List;
import java.util.Random;


public class Placement {

    @NonNull
    private final Random mRandom;
    private final boolean mAllowAdjacentShips;

    public Placement(@NonNull Random random, boolean allowAdjacentShips) {
        mRandom = random;
        mAllowAdjacentShips = allowAdjacentShips;
    }

    @NonNull
    public Board newBoardWithShips(@NonNull Collection<Ship> ships) {
        Board board = new Board();
        populateBoardWithShips(board, ships);
        return board;
    }

    public void populateBoardWithShips(@NonNull Board board, @NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            if (!putShipOnBoard(ship, board)) {
                throw new IllegalArgumentException("could not put " + ship + " on " + BoardUtils.debugBoard(board));
            }
        }
    }

    public boolean putShipOnBoard(@NonNull Ship ship, @NonNull Board board) {
        List<Vector> freeCells = BoardUtils.getCoordinatesFreeFromShips(board, mAllowAdjacentShips);

        while (!freeCells.isEmpty()) {
            int cellIndex = mRandom.nextInt(freeCells.size());
            Vector coordinate = freeCells.get(cellIndex);
            if (BoardUtils.shipFitsTheBoard(ship, coordinate)) {
                if (freeCells.containsAll(ShipUtils.getShipCoordinates(ship, coordinate))) {
                    board.addShip(ship, coordinate);
                    return true;
                }
            }
            freeCells.remove(cellIndex);
        }

        return false;
    }

}
