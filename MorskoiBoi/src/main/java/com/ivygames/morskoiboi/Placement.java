package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.boardsetup.BoardUtils;

import org.commons.logger.Ln;

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
        List<Vector2> freeCells = BoardUtils.getCellsFreeFromShips(board, mAllowAdjacentShips);

        while (!freeCells.isEmpty()) {
            int cellIndex = mRandom.nextInt(freeCells.size());
            Vector2 cell = freeCells.get(cellIndex);
            int i = cell.x;
            int j = cell.y;
            if (board.shipFitsTheBoard(ship, cell) && isPlaceEmpty(ship, board, i, j, freeCells)) {
                putShipAt(board, ship, cell);
                return true;
            } else {
                // this cell is not suitable for placement
                freeCells.remove(cellIndex);
            }
        }

        return false;
    }

    public static void putShipAt(@NonNull Board board, @NonNull Ship ship, @NonNull Vector2 v) {
        putShipAt(board, ship, v.x, v.y);
    }

    // TODO: when ship has no coordinates this method is not needed, use Board#addShip
    public static void putShipAt(@NonNull Board board, @NonNull Ship ship, int i, int j) {
        if (!board.shipFitsTheBoard(ship, i, j)) {
            throw new IllegalArgumentException("cannot put ship " + ship + " at (" + i + "," + j + ")");
        }

        // TODO: if it is exactly the same ship, remove and put again
        ship.setCoordinates(i, j);

//        Collection<Vector2> neighboringCells = BoardUtils.getCells(ship, true);
//        for (Vector2 v : neighboringCells) {
//            if (!mRules.allowAdjacentShips()) {
//                if (ship.isDead()) {
//                    board.setCell(Cell.MISS, v);
//                } else {
//                    board.setCell(Cell.RESERVED, v);
//                }
//            }
//        }

        board.addShip(ship);
    }

    @Nullable
    public static Ship pickShipFromBoard(@NonNull Board board, int i, int j) {
        if (!Board.contains(i, j)) {
            Ln.w("(" + i + "," + j + ") is outside the board");
            return null;
        }

        Board.LocatedShip locatedShip = board.getFirstShipAt(i, j);
        if (locatedShip != null) {
            board.removeShip(locatedShip.ship);
            return locatedShip.ship;
        }
        return null;
    }

    public static void rotateShipAt(@NonNull Board board, int x, int y) {
        if (!Board.contains(x, y)) {
            Ln.w("(" + x + "," + y + ") is outside the board");
            return;
        }

        Ship ship = pickShipFromBoard(board, x, y);
        if (ship == null) {
            return;
        }

        ship.rotate();

        if (board.shipFitsTheBoard(ship, x, y)) {
            putShipAt(board, ship, x, y);
        } else {
            if (ship.isHorizontal()) {
                putShipAt(board, ship, board.horizontalDimension() - ship.size, y);
            } else {
                putShipAt(board, ship, x, board.horizontalDimension() - ship.size);
            }
        }
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
