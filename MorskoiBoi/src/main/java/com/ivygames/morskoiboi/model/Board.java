package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.ai.PlacementFactory;

import org.commons.logger.Ln;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Board {

    public static final int DIMENSION = 10;

    private static final String CELLS = "cells";
    private static final String SHIPS = "ships";
    private Collection<Ship> mShips;
    private Cell[][] mCells;

    @NonNull
    public static Board fromJson(@NonNull String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return Board.fromJson(jsonObject);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @NonNull
    public static Board fromJson(@NonNull JSONObject jsonObject) {
        Board board = new Board();

        try {
            Board.populateCellsFromString(board.mCells, jsonObject.getString(CELLS));
            Board.populateShipsFromJson(board, jsonObject.getJSONArray(SHIPS));
        } catch (JSONException e) {
            Ln.e(e);
            throw new IllegalArgumentException(e);
        }

        return board;
    }

    private static void populateCellsFromString(@NonNull Cell[][] cells, @NonNull String cellsString) {
        int columns = cells.length;
        for (int i = 0; i < columns; i++) {
            int rows = cells[i].length;
            for (int j = 0; j < rows; j++) {
                cells[i][j] = Cell.parse(cellsString.charAt(i * columns + j));
            }
        }
    }

    private static void populateShipsFromJson(@NonNull Board board, @NonNull JSONArray shipsJson) throws JSONException {
        for (int i = 0; i < shipsJson.length(); i++) {
            JSONObject shipJson = shipsJson.getJSONObject(i);
            Ship ship = Ship.fromJson(shipJson);
            PlacementFactory.getAlgorithm().putShipAt(board, ship, ship.getX(), ship.getY());
        }
    }

    private static String getStringFromCells(@NonNull Cell[][] cells) {
        StringBuilder sb = new StringBuilder(200);

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                sb.append(cells[i][j].toChar());
            }
        }

        return sb.toString();
    }

    @NonNull
    public static JSONObject toJson(@NonNull Board board) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CELLS, Board.getStringFromCells(board.mCells));

            JSONArray shipsJson = new JSONArray();
            for (Ship ship : board.mShips) {
                shipsJson.put(ship.toJson());
            }
            jsonObject.put(SHIPS, shipsJson);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    public Board() {
        clearBoard();
    }

    public Collection<Ship> getShips() {
        return mShips;
    }

    /**
     * @return all cells that will return true on {@link Cell#isEmpty()}
     */
    @NonNull
    public List<Vector2> getEmptyCells() {
        List<Vector2> emptyCells = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (mCells[i][j].isEmpty()) {
                    emptyCells.add(Vector2.get(i, j));
                }
            }
        }
        return emptyCells;
    }

    public boolean shipFitsTheBoard(@NonNull Ship ship, @NonNull Vector2 aim) {
        return shipFitsTheBoard(ship, aim.getX(), aim.getY());
    }

    /**
     * does not check if cells are empty
     *
     * @return true if the ship can be layed out on the board
     */
    public boolean shipFitsTheBoard(@NonNull Ship ship, int i, int j) {
        boolean canPut = containsCell(i, j);

        if (canPut) {
            if (ship.isHorizontal()) {
                canPut = i + ship.getSize() <= DIMENSION;
            } else {
                canPut = j + ship.getSize() <= DIMENSION;
            }
        }
        return canPut;
    }

    public static boolean containsCell(@NonNull Vector2 aim) {
        return containsCell(aim.getX(), aim.getY());
    }

    public static boolean containsCell(int i, int j) {
        return i < DIMENSION && i >= 0 && j < DIMENSION && j >= 0;
    }

    /**
     * @throws IndexOutOfBoundsException when trying to access cell outside of the board
     */
    public Cell getCell(int x, int y) {
        return mCells[x][y];
    }

    public Cell getCellAt(@NonNull Vector2 vector) {
        return getCell(vector.getX(), vector.getY());
    }

    private static void putShips(@NonNull Board board, @NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            putShip(board, ship);
        }
    }

    private static void putShip(@NonNull Board board, @NonNull Ship ship) {
        PlacementFactory.getAlgorithm().putShipAt(board, ship, ship.getX(), ship.getY());
    }

    /**
     * clears cells and ships from the board - like a new board
     */
    public void clearBoard() {
        mCells = createNewBoard();
        mShips = new ArrayList<>();
    }

    /**
     * @return null if no ship at (x,y) was found
     * @throws IllegalArgumentException if (x,y) is outside the board
     */
    @Nullable
    public Ship removeShipFrom(int x, int y) { // TODO: bad, very bad method
        if (!containsCell(x, y)) {
            // throw new IllegalArgumentException("(" + x + "," + y +
            // ") is outside the board");
            Ln.w("(" + x + "," + y + ") is outside the board");
            return null;
        }

        // find the ship to remove
        Ship removedShip = getShipAt(x, y);

        // if the ship found - recreate the board without this ship
        if (removedShip != null) {
            // missed and hit cells are not recreated by adding ships back, so
            // we need to remember them
            List<Vector2> missedList = new LinkedList<>();
            List<Vector2> hitList = new LinkedList<>();
            for (int i = 0; i < DIMENSION; i++) {
                for (int j = 0; j < DIMENSION; j++) {
                    Cell cell = mCells[i][j];
                    Vector2 vector = Vector2.get(i, j);
                    if (cell.isMiss()) {
                        missedList.add(vector);
                    } else if (cell.isHit()) {
                        hitList.add(vector);
                    }
                }
            }

            // clear the board and add the rest of the ships
            mShips.remove(removedShip);
            Collection<Ship> ships = mShips;
            clearBoard();
            putShips(this, ships);

            for (Vector2 missPlace : missedList) {
                mCells[missPlace.getX()][missPlace.getY()].setMiss();
            }

            for (Vector2 hitPlace : hitList) {
                mCells[hitPlace.getX()][hitPlace.getY()].setHit();
            }
        }

        return removedShip;
    }

    public void rotateShipAt(int x, int y) {
        if (!containsCell(x, y)) {
            Ln.w("(" + x + "," + y + ") is outside the board");
            return;
        }

        Ship ship = removeShipFrom(x, y);
        if (ship == null) {
            return;
        }

        ship.rotate();

        Placement algorithm = PlacementFactory.getAlgorithm();
        if (shipFitsTheBoard(ship, x, y)) {
            algorithm.putShipAt(this, ship, x, y); // FIXME: ship.getX(), ship.getY(). // what did I mean here?
        } else {
            if (ship.isHorizontal()) {
                algorithm.putShipAt(this, ship, getHorizontalDim() - ship.getSize(), y);
            } else {
                algorithm.putShipAt(this, ship, x, getHorizontalDim() - ship.getSize());
            }
        }
    }

    @NonNull
    public Collection<Ship> getShipsAt(@NonNull Vector2 vector) {
        return getShipsAt(vector.getX(), vector.getY());
    }

    @NonNull
    public Collection<Ship> getShipsAt(int i, int j) {
        HashSet<Ship> ships = new HashSet<>();
        if (hasShipAt(i, j)) {
            for (Ship ship : mShips) {
                if (ship.isInShip(i, j)) {
                    ships.add(ship);
                }
            }
        }

        return ships;
    }

    private boolean hasShipAt(int i, int j) {
        Cell cell = getCell(i, j);
        return cell.isReserved() || cell.isHit()/* || cell.isSunk() */;
    }

    @Nullable
    public Ship getFirstShipAt(@NonNull Vector2 vector) {
        return getFirstShipAt(vector.getX(), vector.getY());
    }

    @Nullable
    private Ship getFirstShipAt(int i, int j) {
        if (hasShipAt(i, j)) {
            for (Ship ship : mShips) {
                if (ship.isInShip(i, j)) {
                    return ship;
                }
            }
        }

        return null;
    }

    /**
     * @return the first ship that contains the coordinate or null if no such ship found
     */
    // TODO: what is the difference from getFirstShipAt?
    @Nullable
    private Ship getShipAt(int x, int y) {
        for (Ship ship : mShips) {
            if (ship.isInShip(x, y)) {
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
                cells[i][j] = new Cell(); // NOPMD
            }
        }

        return cells;
    }

    public int getHorizontalDim() {
        return DIMENSION;
    }

    public int getVerticalDim() {
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
        if (!Arrays.deepEquals(mCells, other.mCells)) {
            return false;
        }
        if (mShips == null) {
            if (other.mShips != null) {
                return false;
            }
        } else if (!mShips.equals(other.mShips)) {
            return false;
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

}
