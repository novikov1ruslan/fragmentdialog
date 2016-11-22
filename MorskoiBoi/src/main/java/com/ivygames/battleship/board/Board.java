package com.ivygames.battleship.board;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.ShipUtils;

import org.apache.commons.collections4.collection.UnmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {

    public static final int DIMENSION = 10;

    @NonNull
    private final Collection<LocatedShip> mShips = new ArrayList<>();
    @NonNull
    Cell[][] mCells = createNewBoard();

    @NonNull
    public Collection<Ship> getShips() {
        Collection<Ship> ships = new HashSet<>(mShips.size());
        for (LocatedShip locatedShip : mShips) {
            ships.add(locatedShip.ship);
        }
        return UnmodifiableCollection.unmodifiableCollection(ships);
    }

    @NonNull
    public Collection<LocatedShip> getLocatedShips() {
        return UnmodifiableCollection.unmodifiableCollection(mShips);
    }

    public boolean removeShip(@NonNull LocatedShip locatedShip) {
        return mShips.remove(locatedShip);
    }

    public void addShip(@NonNull LocatedShip locatedShip) {
        int i = locatedShip.position.x;
        int j = locatedShip.position.y;
        if (!shipFitsTheBoard(locatedShip.ship, i, j)) {
            throw new IllegalArgumentException("cannot put ship " + locatedShip);
        }
        mShips.add(locatedShip);
    }

    @NonNull
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
                canPut = i + ship.size <= DIMENSION;
            } else {
                canPut = j + ship.size <= DIMENSION;
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
        for (LocatedShip locatedShip : mShips) {
            if (ShipUtils.isInShip(v, locatedShip)) {
                ships.add(locatedShip.ship);
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
    public LocatedShip getFirstShipAt(@NonNull Vector2 v) {
        for (LocatedShip locatedShip : mShips) {
            if (ShipUtils.isInShip(v, locatedShip)) {
                return locatedShip;
            }
        }

        return null;
    }

    @Nullable
    public LocatedShip getFirstShipAt(int i, int j) {
        return getFirstShipAt(Vector2.get(i, j));
    }

    @NonNull
    private static Cell[][] createNewBoard() {
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
        // TODO: optimize iterating over Located or move the method from here
        for (Ship ship : board.getShips()) {
            if (!ship.isDead()) {
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

}
