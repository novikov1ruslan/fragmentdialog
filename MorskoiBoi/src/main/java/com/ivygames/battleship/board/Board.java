package com.ivygames.battleship.board;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;

import org.apache.commons.collections4.set.UnmodifiableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {

    public static final int DIMENSION = 10;

    @NonNull
    Cell[][] mCells = createNewBoard();

    @NonNull
    private final Set<LocatedShip> mShips = new HashSet<>();

    public int width() {
        return DIMENSION;
    }

    public int height() {
        return DIMENSION;
    }

    @NonNull
    public Set<Ship> getShips() {
        Set<Ship> ships = new HashSet<>(mShips.size());
        for (LocatedShip locatedShip : mShips) {
            ships.add(locatedShip.ship);
        }
        return UnmodifiableSet.unmodifiableSet(ships);
    }

    @NonNull
    public Set<LocatedShip> getLocatedShips() {
        return UnmodifiableSet.unmodifiableSet(mShips);
    }

    public boolean removeShip(@NonNull Ship ship) {
        return removeShip(new LocatedShip(ship));
    }

    private boolean removeShip(@NonNull LocatedShip locatedShip) {
        return mShips.remove(locatedShip);
    }

    public void addShip(@NonNull Ship ship, int i, int j) {
        addShip(new LocatedShip(ship, i, j));
    }

    public void addShip(@NonNull LocatedShip locatedShip) {
        int i = locatedShip.coordinate.x;
        int j = locatedShip.coordinate.y;
        if (!BoardUtils.shipFitsTheBoard(locatedShip.ship, i, j)) {
            throw new IllegalArgumentException("cannot put ship " + locatedShip);
        }
        mShips.add(locatedShip);
    }

    @NonNull
    public List<Vector> getCellsByType(@NonNull Cell cell) {
        List<Vector> cells = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (mCells[i][j] == cell) {
                    cells.add(Vector.get(i, j));
                }
            }
        }
        return cells;
    }

    @NonNull
    public Cell getCell(@NonNull Vector v) {
        return getCell(v.x, v.y);
    }

    /**
     * @throws IndexOutOfBoundsException when trying to access cell outside of the board
     */
    @NonNull
    public Cell getCell(int i, int j) {
        return mCells[i][j];
    }

    public void setCell(@NonNull Cell cell, @NonNull Vector v) {
        setCell(cell, v.x, v.y);
    }

    public void setCell(@NonNull Cell cell, int i, int j) {
        mCells[i][j] = cell;
    }

    @NonNull
    public Collection<Ship> getShipsAt(@NonNull Vector v) {
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
        return getShipsAt(Vector.get(i, j));
    }

    @Nullable
    public LocatedShip getShipAt(@NonNull Vector v) {
        for (LocatedShip locatedShip : mShips) {
            if (ShipUtils.isInShip(v, locatedShip)) {
                return locatedShip;
            }
        }

        return null;
    }

    @Nullable
    public LocatedShip getShipAt(int i, int j) {
        return getShipAt(Vector.get(i, j));
    }

    public boolean hasShipAt(@NonNull Vector v) {
        return hasShipAt(v.x, v.y);
    }

    public boolean hasShipAt(int i, int j) {
        return getShipAt(i, j) != null;
    }

    /**
     * clears cells and ships from the board - like a new board
     */
    public void clearBoard() {
        mCells = createNewBoard();
        mShips.clear();
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
        for (LocatedShip locatedShip : getLocatedShips()) {
            board.append(locatedShip).append("; ");
        }
        board.append('\n');

        return board.toString();
    }

}
