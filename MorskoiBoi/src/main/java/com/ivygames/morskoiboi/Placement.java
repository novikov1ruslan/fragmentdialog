package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.LocatedShip;
import com.ivygames.battleship.board.Vector2;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.screen.boardsetup.BoardUtils;

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

    // TODO: remove
    public void populateBoardWithShips(@NonNull Board board, @NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            putShipOnBoard(ship, board);
        }
    }

    public boolean putShipOnBoard(@NonNull Ship ship, @NonNull Board board) {
        List<Vector2> freeCells = BoardUtils.getCoordinatesFreeFromShips(board, mAllowAdjacentShips);

        while (!freeCells.isEmpty()) {
            int cellIndex = mRandom.nextInt(freeCells.size());
            Vector2 cell = freeCells.get(cellIndex);
            int i = cell.x;
            int j = cell.y;
            if (BoardUtils.shipFitsTheBoard(ship, cell) && isPlaceEmpty(ship, board, i, j, freeCells)) {
                board.addShip(new LocatedShip(ship, i, j));
                return true;
            } else {
                // this cell is not suitable for placement
                freeCells.remove(cellIndex);
            }
        }

        return false;
    }

    /**
     * @return true if the {@code board} has empty space for the {@code ship} at coordinates ({@code i},{@code j}
     */
    private static boolean isPlaceEmpty(@NonNull Ship ship, @NonNull Board board, int i, int j, @NonNull Collection<Vector2> freeCells) {
        if (board.getCell(i, j) != Cell.EMPTY) {
            return false;
        }

        boolean isHorizontal = ship.isHorizontal();
        for (int k = isHorizontal ? i : j; k < (isHorizontal ? i : j) + ship.size; k++) {
            int x = isHorizontal ? k : i;
            int y = isHorizontal ? j : k;
            if (!freeCells.contains(Vector2.get(x, y))) {
                return false;
            }
        }

        return true;
    }
}
