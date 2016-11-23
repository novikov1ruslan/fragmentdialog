package com.ivygames.battleship;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.board.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.screen.boardsetup.CoordinateType;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardUtils {

    @NonNull
    public static List<Vector> getNeighboringCoordinates(int x, int y) {
        return getCoordinates(new LocatedShip(new Ship(1), Vector.get(x, y)), CoordinateType.NEAR_SHIP);
    }

    @NonNull
    public static List<Vector> getCoordinates(@NonNull LocatedShip locatedShip, @NonNull CoordinateType type) {
        List<Vector> coordinates = new ArrayList<>();

        int x = locatedShip.coordinate.x;
        int y = locatedShip.coordinate.y;
        Ship ship = locatedShip.ship;
        boolean horizontal = ship.isHorizontal();

        for (int i = -1; i <= ship.size; i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                if (contains(cellX, cellY)) {
                    Vector v = Vector.get(cellX, cellY);
                    boolean inShip = ShipUtils.isInShip(v, locatedShip);
                    if (inShip && !type.isNeighboring()) {
                        coordinates.add(v);
                    } else if (!inShip && type.isNeighboring()) {
                        coordinates.add(v);
                    }
                }
            }
        }

        return coordinates;
    }

    @NonNull
    public static List<Vector> getCoordinatesFreeFromShips(@NonNull Board board, boolean allowAdjacentShips) {
        List<Vector> coordinates = Vector.getAllCoordinates();
        for (LocatedShip locatedShip : board.getLocatedShips()) {
            coordinates.removeAll(getCoordinates(locatedShip, CoordinateType.IN_SHIP));
            if (!allowAdjacentShips) {
                coordinates.removeAll(getCoordinates(locatedShip, CoordinateType.NEAR_SHIP));
            }
        }
        return coordinates;
    }

    @NonNull
    public static List<Vector> getPossibleShots(@NonNull Board board, boolean allowAdjacentShips) {
        List<Vector> cells = getCoordinatesFreeFromShips(board, allowAdjacentShips);
        cells.removeAll(board.getCellsByType(Cell.HIT));
        cells.removeAll(board.getCellsByType(Cell.MISS));
        return cells;
    }

    public static boolean isCellConflicting(@NonNull Board board, int i, int j, boolean allowAdjacentShips) {
        Collection<Ship> theseShips = board.getShipsAt(i, j);
        if (theseShips.isEmpty()) {
            return false;
        }

        if (theseShips.size() > 1) {
            return true;
        }

        if (!allowAdjacentShips) {
            Ship ship = theseShips.iterator().next();

            Collection<Vector> coordinates = getNeighboringCoordinates(i, j);
            for (Vector v : coordinates) {
                Collection<Ship> otherShips = board.getShipsAt(v);
                for (Ship otherShip : otherShips) {
                    if (otherShip != ship) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isBoardSet(@NonNull Board board, @NonNull Rules rules, int numberOfShips) {
        return allShipsAreOnBoard(board, numberOfShips) && !hasConflictingCell(board, rules.allowAdjacentShips());
    }

    private static boolean allShipsAreOnBoard(@NonNull Board board, int numberOfShips) {
        return board.getShips().size() == numberOfShips;
    }

    public static boolean hasConflictingCell(@NonNull Board board, boolean allowAdjacentShips) {
        for (int i = 0; i < board.width(); i++) {
            for (int j = 0; j < board.height(); j++) {
                if (isCellConflicting(board, i, j, allowAdjacentShips)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return true if board has 10 ships and all of them are destroyed
     */
    public static boolean isItDefeatedBoard(@NonNull Board board, int numberOfShips) {
        return allShipsAreOnBoard(board, numberOfShips) && allAvailableShipsAreDestroyed(board);
    }

    @Nullable
    public static Ship pickShipFromBoard(@NonNull Board board, int i, int j) {
        if (!contains(i, j)) {
            Ln.w("(" + i + "," + j + ") is outside the board");
            return null;
        }

        LocatedShip locatedShip = board.getShipAt(i, j);
        if (locatedShip != null) {
            board.removeShip(locatedShip);
            return locatedShip.ship;
        }
        return null;
    }

    public static void rotateShipAt(@NonNull Board board, int x, int y) {
        if (!contains(x, y)) {
            Ln.w("(" + x + "," + y + ") is outside the board");
            return;
        }

        Ship ship = pickShipFromBoard(board, x, y);
        if (ship == null) {
            return;
        }

        ship.rotate();

        if (shipFitsTheBoard(ship, x, y)) {
            board.addShip(new LocatedShip(ship, x, y));
        } else {
            int i = board.width() - ship.size;
            if (ship.isHorizontal()) {
                board.addShip(new LocatedShip(ship, i, y));
            } else {
                board.addShip(new LocatedShip(ship, x, i));
            }
        }
    }

    /**
     * @param v coordinate on the board where the 1st ship's square is to be put
     */
    public static boolean shipFitsTheBoard(@NonNull Ship ship, @NonNull Vector v) {
        return shipFitsTheBoard(ship, v.x, v.y);
    }

    /**
     * does not check if cells are empty
     *
     * @param i horizontal coordinate on the board where the 1st ship's square is to be put
     * @param j vertical coordinate on the board where the 1st ship's square is to be put
     * @return true if the ship can fit out on the board
     */
    public static boolean shipFitsTheBoard(@NonNull Ship ship, int i, int j) {
        boolean canPut = contains(i, j);

        if (canPut) {
            if (ship.isHorizontal()) {
                canPut = i + ship.size <= Board.DIMENSION;
            } else {
                canPut = j + ship.size <= Board.DIMENSION;
            }
        }
        return canPut;
    }

    public static boolean contains(@NonNull Vector v) {
        return contains(v.x, v.y);
    }

    public static boolean contains(int i, int j) {
        return i < Board.DIMENSION && i >= 0 && j < Board.DIMENSION && j >= 0;
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
}
