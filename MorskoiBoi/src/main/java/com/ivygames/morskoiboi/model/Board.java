package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.ShipUtils;

import org.apache.commons.collections4.collection.UnmodifiableCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Board {

    public static final int DIMENSION = 10;

    @NonNull
    private final Collection<Ship> mShips = new ArrayList<>();
    Cell[][] mCells;

    public Board() {
        clearBoard();
    }

    @NonNull
    public Collection<Ship> getShips() {
        return UnmodifiableCollection.unmodifiableCollection(mShips);
    }

    public boolean removeShip(@NonNull Ship ship) {
        return mShips.remove(ship);
    }

    public void addShip(@NonNull Ship ship) {
        mShips.add(ship);
    }

    public List<Vector2> getCellsByType(@NonNull Cell cell) {
        List<Vector2> cells = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (mCells[i][j] == cell) {
                    cells.add(Vector2.get(i, j));
                }
            }
        }
        return cells;
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

    @NonNull
    public Cell getCell(@NonNull Vector2 v) {
        return getCell(v.getX(), v.getY());
    }

    /**
     * @throws IndexOutOfBoundsException when trying to access cell outside of the board
     */
    @NonNull
    public Cell getCell(int i, int j) {
        return mCells[i][j];
    }

    @NonNull
    public Cell getCellAt(@NonNull Vector2 v) {
        return getCell(v.getX(), v.getY());
    }

    /**
     * clears cells and ships from the board - like a new board
     */
    public void clearBoard() {
        mCells = createNewBoard();
        mShips.clear();
    }

    @NonNull
    public Collection<Ship> getShipsAt(@NonNull Vector2 v) {
        return getShipsAt(v.getX(), v.getY());
    }

    @NonNull
    public Collection<Ship> getShipsAt(int i, int j) {
        Set<Ship> ships = new HashSet<>();
        for (Ship ship : mShips) {
            if (Ship.isInShip(ship, i, j)) {
                ships.add(ship);
            }
        }

        return ships;
    }

    public boolean hasShipAt(@NonNull Vector2 v) {
        return getFirstShipAt(v.getX(), v.getY()) != null;
    }

    @Nullable
    public Ship getFirstShipAt(@NonNull Vector2 v) {
        return getFirstShipAt(v.getX(), v.getY());
    }

    @Nullable
    public Ship getFirstShipAt(int i, int j) {
        for (Ship ship : mShips) {
            if (Ship.isInShip(ship, i, j)) {
                return ship;
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

    public void setCell(@NonNull Cell cell, @NonNull Vector2 v) {
        setCell(cell, v.getX(), v.getY());
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
        if (!equals(mShips, other.mShips)) {
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
        for (Ship ship : getShips()) {
            board.append(ship).append("; ");
        }
        board.append('\n');

        return board.toString();
    }

    private static class LocatedShip {

        private final Ship ship;
        private final Vector2 v;

        LocatedShip(Ship ship, Vector2 v) {
            this.ship = ship;
            this.v = v;
        }

    }

}
