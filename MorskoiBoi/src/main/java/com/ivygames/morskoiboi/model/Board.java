package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.ShipUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Board {

    public static final int DIMENSION = 10;

    Collection<Ship> mShips;
    Cell[][] mCells;

    public Board() {
        clearBoard();
    }

    public Collection<Ship> getShips() {
        return mShips;
    }

    public boolean removeShip(@NonNull Ship ship) {
        return mShips.remove(ship);
    }

    /**
     * @return all cells that will return true on {@link Cell#EMPTY}
     */
    @NonNull
    public List<Vector2> getEmptyCells() {
        List<Vector2> emptyCells = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (mCells[i][j] == Cell.EMPTY) {
                    emptyCells.add(Vector2.get(i, j));
                }
            }
        }
        return emptyCells;
    }

    /**
     * @param v coordinate on the board where the 1st ship's square is to be put
     */
    public boolean shipFitsTheBoard(@NonNull Ship ship, @NonNull Vector2 v) {
        return shipFitsTheBoard(ship, v.getX(), v.getY());
    }

    /**
     * does not check if cells are empty
     *
     * @param i horizontal coordinate on the board where the 1st ship's square is to be put
     * @param j vertical coordinate on the board where the 1st ship's square is to be put
     * @return true if the ship can fit out on the board
     */
    public boolean shipFitsTheBoard(@NonNull Ship ship, int i, int j) {
        boolean canPut = contains(i, j);

        if (canPut) {
            if (ship.isHorizontal()) {
                canPut = i + ship.getSize() <= DIMENSION;
            } else {
                canPut = j + ship.getSize() <= DIMENSION;
            }
        }
        return canPut;
    }

    public static boolean contains(@NonNull Vector2 v) {
        return contains(v.getX(), v.getY());
    }

    public static boolean contains(int i, int j) {
        return i < DIMENSION && i >= 0 && j < DIMENSION && j >= 0;
    }

    public Cell getCell(@NonNull Vector2 v) {
        return getCell(v.getX(), v.getY());
    }

    /**
     * @throws IndexOutOfBoundsException when trying to access cell outside of the board
     */
    public Cell getCell(int i, int j) {
        return mCells[i][j];
    }

    public Cell getCellAt(@NonNull Vector2 vector) {
        return getCell(vector.getX(), vector.getY());
    }

    /**
     * clears cells and ships from the board - like a new board
     */
    public void clearBoard() {
        mCells = createNewBoard();
        mShips = new ArrayList<>();
    }

    @NonNull
    public Collection<Ship> getShipsAt(@NonNull Vector2 vector) {
        return getShipsAt(vector.getX(), vector.getY());
    }

    @NonNull
    public Collection<Ship> getShipsAt(int i, int j) {
        HashSet<Ship> ships = new HashSet<>();
        if (canHaveShipAt(i, j)) {
            for (Ship ship : mShips) {
                if (ship.isInShip(i, j)) {
                    ships.add(ship);
                }
            }
        }

        return ships;
    }

    private boolean canHaveShipAt(int i, int j) {
        Cell cell = getCell(i, j);
        return cell != Cell.MISS && cell != Cell.EMPTY;
    }

    public boolean hasShipAt(@NonNull Vector2 coordinate) {
        return hasShipAt(coordinate.getX(), coordinate.getY());
    }

    public boolean hasShipAt(int i, int j) {
        return getFirstShipAt(i, j) != null;
    }

    @Nullable
    public Ship getFirstShipAt(@NonNull Vector2 vector) {
        return getFirstShipAt(vector.getX(), vector.getY());
    }

    @Nullable
    public Ship getFirstShipAt(int i, int j) {
        if (canHaveShipAt(i, j)) {
            for (Ship ship : mShips) {
                if (ship.isInShip(i, j)) {
                    return ship;
                }
            }
        }

        return null;
    }

    @NonNull
    private Cell[][] createNewBoard() {
        Cell[][] cells = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = Cell.EMPTY;
            }
        }

        return cells;
    }

    public int horizontalDimension() {
        return DIMENSION;
    }

    public int verticalDimension() {
        return DIMENSION;
    }

    public void setCell(@NonNull Cell cell, @NonNull Vector2 vector) {
        setCell(cell, vector.getX(), vector.getY());
    }

    public void setCell(@NonNull Cell cell, int i, int j) {
        mCells[i][j] = cell;
    }

    /**
     * @return true if every ship on the board is sunk
     */
    public static boolean allAvailableShipsAreDestroyed(@NonNull Board board) {
        for (Ship ship : board.getShips()) {
            if (!ship.isDead()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(mCells);
        result = prime * result + ((mShips == null) ? 0 : mShips.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Board other = (Board) obj;
        if (!equals(mCells, other.mCells)) {
            return false;
        }
        if (mShips == null) {
            if (other.mShips != null) {
                return false;
            }
        } else if (!equals(mShips, other.mShips)) {
            return false;
        }
        return true;
    }

    // TODO: remove when cell becomes immutable
    private boolean equals(Cell[][] cells1, Cell[][] cells2) {
        if (cells1.length != cells2.length) {
            return false;
        }
        for (int i = 0; i < cells1.length; i++) {
            if (cells1[i].length != cells2[i].length) {
                return false;
            }
            for (int j = 0; j < cells1[i].length; j++) {
                Cell cell1 = cells1[i][j];
                Cell cell2 = cells2[i][j];
                if (cell1.toChar() != cell2.toChar()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean equals(@NonNull Collection<Ship> ships1, @NonNull Collection<Ship> ships2) {
        if (ships1.size() != ships2.size()) {
            return false;
        }

        Iterator<Ship> iterator = ships2.iterator();
        for (Ship ship : ships1) {
            if (!ShipUtils.similar(ship, iterator.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder(130);
        board.append("----------\n");
        // print rows first
        for (int y = 0; y < DIMENSION; y++) {
            for (int x = 0; x < DIMENSION; x++) {
                board.append(mCells[x][y]);
            }
            board.append('\n');
        }
        for (Ship ship : mShips) {
            board.append(ship).append("; ");
        }
        board.append('\n');

        return board.toString();
    }

    public void addShip(@NonNull Ship ship) {
        mShips.add(ship);
    }
}
