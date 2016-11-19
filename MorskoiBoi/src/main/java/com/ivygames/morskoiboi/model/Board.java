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
    private final Collection<LocatedShip> mShips = new ArrayList<>();
    Cell[][] mCells;

    public Board() {
        clearBoard();
    }

    @NonNull
    public Collection<Ship> getShips() {
        Collection<Ship> ships = new HashSet<>(mShips.size());
        for (LocatedShip ship : mShips) {
            ships.add(ship.ship);
        }
        return UnmodifiableCollection.unmodifiableCollection(ships);
    }

    public boolean removeShip(@NonNull Ship ship) {
        return mShips.remove(new LocatedShip(ship, ship.getPosition()));
    }

    public void addShip(@NonNull Ship ship) {
        mShips.add(new LocatedShip(ship, ship.getPosition()));
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
        return shipFitsTheBoard(ship, v.x, v.y);
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
        return contains(v.x, v.y);
    }

    public static boolean contains(int i, int j) {
        return i < DIMENSION && i >= 0 && j < DIMENSION && j >= 0;
    }

    @NonNull
    public Cell getCell(@NonNull Vector2 v) {
        return getCell(v.x, v.y);
    }

    /**
     * @throws IndexOutOfBoundsException when trying to access cell outside of the board
     */
    @NonNull
    public Cell getCell(int i, int j) {
        return mCells[i][j];
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
        Set<Ship> ships = new HashSet<>();
        for (LocatedShip ship : mShips) {
            if (Ship.isInShip(v, ship.ship, ship.position)) {
                ships.add(ship.ship);
            }
        }

        return ships;
    }

    @NonNull
    public Collection<Ship> getShipsAt(int i, int j) {
        return getShipsAt(Vector2.get(i, j));
    }

    public boolean hasShipAt(@NonNull Vector2 v) {
        return getFirstShipAt(v.x, v.y) != null;
    }

    @Nullable
    public Ship getFirstShipAt(@NonNull Vector2 v) {
        for (LocatedShip ship : mShips) {
            if (Ship.isInShip(v, ship.ship)) {
                return ship.ship;
            }
        }

        return null;
    }

    @Nullable
    public Ship getFirstShipAt(int i, int j) {
        return getFirstShipAt(Vector2.get(i, j));
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
        setCell(cell, v.x, v.y);
    }

    void setCell(@NonNull Cell cell, int i, int j) {
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
        result = prime * result + mShips.hashCode();
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
        return equals(mCells, other.mCells) && equals(getShips(), other.getShips());
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

    static class LocatedShip {

        @NonNull
        final Ship ship;
        @NonNull
        final Vector2 position;

        LocatedShip(@NonNull Ship ship, @NonNull Vector2 position) {
            this.ship = ship;
            this.position = position;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LocatedShip that = (LocatedShip) o;

            return ship.equals(that.ship);
        }

        @Override
        public int hashCode() {
            return ship.hashCode();
        }
    }

}
