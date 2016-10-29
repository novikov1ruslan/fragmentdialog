package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Placement {

    @NonNull
    private final Random mRandom;
    @NonNull
    private final Rules mRules;

    public Placement(@NonNull Random random, @NonNull Rules rules) {
        mRandom = random;
        mRules = rules;
    }

    public void populateBoardWithShips(@NonNull Board board, @NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            putShipOnBoard(ship, board);
        }
    }

    private boolean putShipOnBoard(@NonNull Ship ship, @NonNull Board board) {
        List<Vector2> cells = board.getEmptyCells();
        while (!cells.isEmpty()) {
            int cellIndex = mRandom.nextInt(cells.size());
            Vector2 cell = cells.get(cellIndex);
            int i = cell.getX();
            int j = cell.getY();
            if (board.shipFitsTheBoard(ship, i, j)) {
                if (isPlaceEmpty(ship, board, i, j)) {
                    putShipAt(board, ship, i, j);
                    return true;
                } else {
                    // this cell is not suitable for placement
                    cells.remove(cellIndex);
                }
            }
        }

        return false;
    }

    public void putShipAt(@NonNull Board board, @NonNull Ship ship, @NonNull Vector2 v) {
        putShipAt(board, ship, v.getX(), v.getY());
    }

    public void putShipAt(@NonNull Board board, @NonNull Ship ship, int x, int y) {
        if (!board.shipFitsTheBoard(ship, x, y)) {
            throw new IllegalArgumentException("cannot put ship " + ship + " at (" + x + "," + y + ")");
        }

        // TODO: if it is exactly the same ship, remove and put again
        ship.setCoordinates(x, y);

        boolean horizontal = ship.isHorizontal();
        for (int i = -1; i <= ship.getSize(); i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                if (Board.contains(cellX, cellY)) {
                    Cell cell = board.getCell(cellX, cellY);
                    if (ship.isInShip(cellX, cellY)) {
                        cell.addShip();
                        if (ship.isDead()) {
                            cell.setHit();
                        }
                    } else {
                        cell = mRules.setAdjacentCellForShip(ship, cell);
                        board.setCell(cell, cellX, cellY);
                    }
                }
            }
        }

        board.getShips().add(ship);
    }

    private void putShips(@NonNull Board board, @NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            putShipAt(board, ship, ship.getX(), ship.getY());
        }
    }

    public void rotateShipAt(@NonNull Board board, int x, int y) {
        if (!Board.contains(x, y)) {
            Ln.w("(" + x + "," + y + ") is outside the board");
            return;
        }

        Ship ship = removeShipFrom(board, x, y);
        if (ship == null) {
            return;
        }

        ship.rotate();

        if (board.shipFitsTheBoard(ship, x, y)) {
            putShipAt(board, ship, x, y); // FIXME: ship.getX(), ship.getY(). // what did I mean here?
        } else {
            if (ship.isHorizontal()) {
                putShipAt(board, ship, board.horizontalDimension() - ship.getSize(), y);
            } else {
                putShipAt(board, ship, x, board.horizontalDimension() - ship.getSize());
            }
        }
    }

    /**
     * @return null if no ship at (x,y) was found
     * @throws IllegalArgumentException if (x,y) is outside the board
     */
    @Nullable
    public Ship removeShipFrom(@NonNull Board board, int x, int y) { // TODO: bad, very bad method
        if (!Board.contains(x, y)) {
            // throw new IllegalArgumentException("(" + x + "," + y +
            // ") is outside the board");
            Ln.w("(" + x + "," + y + ") is outside the board");
            return null;
        }

        // find the ship to remove
        Ship removedShip = board.getFirstShipAt(x, y);

        // if the ship found - recreate the board without this ship
        if (removedShip != null) {
            // missed and hit cells are not recreated by adding ships back, so
            // we need to remember them
            List<Vector2> missedList = new LinkedList<>();
            List<Vector2> hitList = new LinkedList<>();
            for (int i = 0; i < Board.DIMENSION; i++) {
                for (int j = 0; j < Board.DIMENSION; j++) {
                    Cell cell = board.getCell(i, j);
                    Vector2 vector = Vector2.get(i, j);
                    if (cell.isMiss()) {
                        missedList.add(vector);
                    } else if (cell.isHit()) {
                        hitList.add(vector);
                    }
                }
            }

            // clear the board and add the rest of the ships
            Collection<Ship> ships = board.getShips();
            ships.remove(removedShip);
            board.clearBoard();
            putShips(board, ships);

            for (Vector2 missPlace : missedList) {
                board.getCell(missPlace.getX(), missPlace.getY()).setMiss();
            }

            for (Vector2 hitPlace : hitList) {
                board.getCell(hitPlace.getX(), hitPlace.getY()).setHit();
            }
        }

        return removedShip;
    }

    /**
     * @return true if the {@code board} has empty space for the {@code ship} at coordinates ({@code i},{@code j}
     */
    private static boolean isPlaceEmpty(@NonNull Ship ship, @NonNull Board board, int i, int j) {
        boolean isHorizontal = ship.isHorizontal();
        for (int k = isHorizontal ? i : j; k < (isHorizontal ? i : j) + ship.getSize(); k++) {
            int x = isHorizontal ? k : i;
            int y = isHorizontal ? j : k;
            if (!board.getCell(x, y).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
