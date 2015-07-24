package com.ivygames.morskoiboi.model;

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
import java.util.Set;

public class Board {

    private static final int DIMENSION = 10;

    public static final int TOTAL_HEALTH = 20;

    private static final String CELLS = "cells";
    private static final String SHIPS = "ships";
    private Collection<Ship> mShips;
    private Cell[][] mCells;

    public static Board fromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return Board.fromJson(jsonObject);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Board fromJson(JSONObject jsonObject) throws JSONException {
        Board board = new Board();

        Board.populateCellsFromString(board.mCells, jsonObject.getString(CELLS));
        Board.populateShipsFromJson(board, jsonObject.getJSONArray(SHIPS));

        return board;
    }

    private static void populateCellsFromString(Cell[][] cells, String cellsString) {
        int columns = cells.length;
        for (int i = 0; i < columns; i++) {
            int rows = cells[i].length;
            for (int j = 0; j < rows; j++) {
                cells[i][j] = Cell.parse(cellsString.charAt(i * columns + j));
            }
        }
    }

    private static void populateShipsFromJson(Board board, JSONArray shipsJson) throws JSONException {
        for (int i = 0; i < shipsJson.length(); i++) {
            JSONObject shipJson = shipsJson.getJSONObject(i);
            Ship ship = Ship.fromJson(shipJson);
            board.putShipAt(ship, ship.getX(), ship.getY());
        }
    }

    public Board() {
        clearBoard();
    }

    private static String getStringFromCells(Cell[][] cells) {
        StringBuilder sb = new StringBuilder(200);

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                sb.append(cells[i][j].toChar());
            }
        }

        return sb.toString();
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CELLS, Board.getStringFromCells(mCells));

            JSONArray shipsJson = new JSONArray();
            // [main] java.util.ConcurrentModificationException
            // at java.util.ArrayList$ArrayListIterator.next(ArrayList.java:569)
            // at com.ivygames.morskoiboi.model.Board.toJson(Board.java:81)
            for (Ship ship : mShips) {
                shipsJson.put(ship.toJson());
            }
            jsonObject.put(SHIPS, shipsJson);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    public Collection<Ship> getShips() {
        return mShips;
    }

    /**
     * @return all cells that will return true on {@link Cell#isEmpty()}
     */
    public List<Vector2> getEmptyCells() {
        List<Vector2> emptyCells = new ArrayList<Vector2>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (mCells[i][j].isEmpty()) {
                    emptyCells.add(Vector2.get(i, j));
                }
            }
        }
        return emptyCells;
    }

    /**
     * does not check if cells are empty
     *
     * @return true if the ship can be layed out on the board
     */
    public boolean canPutShipAt(Ship ship, int i, int j) {
        boolean canPut = containsCell(i, j);

        if (canPut) {
            if (ship.isHorizontal()) {
                canPut = i + ship.getSize() <= 10;
            } else {
                canPut = j + ship.getSize() <= 10;
            }
        }
        return canPut;
    }

    public boolean containsCell(int i, int j) {
        return i < DIMENSION && i >= 0 && j < DIMENSION && j >= 0;
    }

    /**
     * @throws IndexOutOfBoundsException when trying to access cell outside of the board
     */
    public Cell getCell(int x, int y) {
        return mCells[x][y];
    }

    public Cell getCellAt(Vector2 vector) {
        return getCell(vector.getX(), vector.getY());
    }

    private void putShips(Collection<Ship> ships) {
        for (Ship ship : ships) {
            putShip(ship);
        }
    }

    private void putShip(Ship ship) {
        putShipAt(ship, ship.getX(), ship.getY());
    }

    /**
     * marks adjacent cells as RESERVED and adds the ship to the board
     *
     * @throws IllegalArgumentException if cannot place the ship
     */
    // TODO: create a version that accepts putShipAt(Ship ship)
    public void putShipAt(Ship ship, int x, int y) {
        if (!canPutShipAt(ship, x, y)) {
            throw new IllegalArgumentException("cannot put ship " + ship + " at (" + x + "," + y + ")");
        }

        // TODO: if it is exactly the same ship, remove and put again
        ship.setX(x);
        ship.setY(y);

        boolean horizontal = ship.isHorizontal();
        for (int i = -1; i <= ship.getSize(); i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                if (containsCell(cellX, cellY)) {
                    Cell cell = getCell(cellX, cellY);
                    if (Ship.isInShip(ship, cellX, cellY)) {
                        cell.addShip();
                        if (ship.isDead()) {
                            // cell.setSunk();
                            cell.setHit();
                        }
                    } else {
                        if (ship.isDead()) {
                            cell.setMiss();
                        } else {
                            cell.setReserved();
                        }
                    }
                }
            }
        }

        mShips.add(ship);
    }

    /**
     * clears cells and ships from the board - like a new board
     */
    private void clearBoard() {
        mCells = createNewBoard();
        mShips = new ArrayList<Ship>();
    }

    /**
     * @return null if no ship at (x,y) was found
     * @throws IllegalArgumentException if (x,y) is outside the board
     */
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
            List<Vector2> missedList = new LinkedList<Vector2>();
            List<Vector2> hitList = new LinkedList<Vector2>();
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
            putShips(ships);

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
        if (canPutShipAt(ship, x, y)) {
            putShipAt(ship, x, y); // FIXME: ship.getX(), ship.getY(). // what did I mean here?
        } else {
            if (ship.isHorizontal()) {
                putShipAt(ship, getHorizontalDim() - ship.getSize(), y);
            } else {
                putShipAt(ship, x, getHorizontalDim() - ship.getSize());
            }
        }
    }

    public Collection<Ship> getShipsAt(Vector2 vector) {
        return getShipsAt(vector.getX(), vector.getY());
    }

    private Collection<Ship> getShipsAt(int i, int j) {
        HashSet<Ship> ships = new HashSet<Ship>();
        if (hasShipAt(i, j)) {
            for (Ship ship : mShips) {
                if (Ship.isInShip(ship, i, j)) {
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

    public Ship getFirstShipAt(Vector2 vector) {
        return getFirstShipAt(vector.getX(), vector.getY());
    }

    private Ship getFirstShipAt(int i, int j) {
        if (hasShipAt(i, j)) {
            for (Ship ship : mShips) {
                if (Ship.isInShip(ship, i, j)) {
                    return ship;
                }
            }
        }

        return null;
    }

    /**
     * @return the first ship that contains the coordinate or null if no such ship found
     */
    private Ship getShipAt(int x, int y) {
        for (Ship ship : mShips) {
            if (Ship.isInShip(ship, x, y)) {
                return ship;
            }
        }

        return null;
    }

    private Cell[][] createNewBoard() {
        Cell[][] cells = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell(); // NOPMD
            }
        }

        return cells;
    }

//    /**
//     * Finds all the cells on the board that are in a conflict with another cell.
//     */
//    public Set<Vector2> getInvalidCells() {
//        Set<Vector2> invalid = new HashSet<Vector2>();
//        for (int i = 0; i < DIMENSION; i++) {
//            for (int j = 0; j < DIMENSION; j++) {
//                if (getCell(i, j).getProximity() > 8) {
//                    invalid.add(Vector2.get(i, j));
//                }
//            }
//        }
//
//        return invalid;
//    }

    // TODO: remove
    public int getHealth() {
        int health = 0;
        for (Ship ship : mShips) {
            health += ship.getHealth();
        }

        return health;
    }

    public int getHorizontalDim() {
        return DIMENSION;
    }

    public int getVerticalDim() {
        return DIMENSION;
    }

    public void setCell(Cell cell, Vector2 vector) {
        setCell(cell, vector.getX(), vector.getY());
    }

    private void setCell(Cell cell, int i, int j) {
        mCells[i][j] = cell;
    }

    /**
     * @return true if every ship on the board is sunk
     */
    private boolean allAvailableShipsAreDestroyed() {
        for (Ship ship : mShips) {
            if (!ship.isDead()) {
                return false;
            }
        }

        return true;
    }

    // TODO: unit test

    /**
     * @return true if board has 10 ships and all of them are destroyed
     */
    public static boolean isItDefeatedBoard(Board board) {
        return board.mShips.size() == 10 && board.allAvailableShipsAreDestroyed();
    }

    public static int countUnshotCells(Board board) {
        int health = 100;
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (board.getCell(i, j).beenShot()) {
                    health--;
                }
            }
        }

        return health;
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
