package com.ivygames.battleship;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardUtils {

    @NonNull
    public static List<Vector> getCoordinatesFreeFromShips(@NonNull Board board, boolean allowAdjacentShips) {
        List<Vector> coordinates = Vector.getAllCoordinates();
        for (LocatedShip locatedShip : board.getLocatedShips()) {
            coordinates.removeAll(ShipUtils.getCoordinates(locatedShip, CoordinateType.IN_SHIP));
            if (!allowAdjacentShips) {
                coordinates.removeAll(ShipUtils.getCoordinates(locatedShip, CoordinateType.NEAR_SHIP));
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
            Ship ship = ShipUtils.any(theseShips);

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

    public static boolean isBoardSet(@NonNull Board board, @NonNull Rules rules) {
        return allShipsAreOnBoard(board, rules.getAllShipsSizes().length) && !hasConflictingCell(board, rules.allowAdjacentShips());
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
            board.removeShip(locatedShip.ship);
            return locatedShip.ship;
        }
        return null;
    }

    public static void rotateShipAt(@NonNull Board board, int x, int y) {
        Ship ship = pickShipFromBoard(board, x, y);
        if (ship == null) {
            return;
        }

        ship.rotate();

        if (shipFitsTheBoard(ship, x, y)) {
            board.addShip(ship, x, y);
        } else {
            int i = board.width() - ship.size;
            if (ship.isHorizontal()) {
                board.addShip(ship, i, y);
            } else {
                board.addShip(ship, x, i);
            }
        }
    }

    /**
     * @param v coordinate on the board where the TOP/LEFT ship's square is to be put
     */
    static boolean shipFitsTheBoard(@NonNull Ship ship, @NonNull Vector v) {
        return shipFitsTheBoard(ship, v.x, v.y);
    }

    /**
     * @param i LEFT ship's coordinate
     * @param j TOP ship's coordinate
     * @return true if the ship can fit out on the board, does not check if cells are empty
     */
    public static boolean shipFitsTheBoard(@NonNull Ship ship, int i, int j) {
        boolean fits = contains(i, j);

        if (fits) {
            if (ship.isHorizontal()) {
                fits = i + ship.size <= Board.DIMENSION;
            } else {
                fits = j + ship.size <= Board.DIMENSION;
            }
        }
        return fits;
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
    private static boolean allAvailableShipsAreDestroyed(@NonNull Board board) {
        for (Ship ship : board.getShips()) {
            if (!ship.isDead()) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    private static List<Vector> getNeighboringCoordinates(int x, int y) {
        return ShipUtils.getCoordinates(new LocatedShip(new Ship(1), Vector.get(x, y)), CoordinateType.NEAR_SHIP);
    }

    @NonNull
    public static Vector findShipLocation(@NonNull Board board) {
        Collection<Vector> hitCells = findHitCellsWithNoShip(board);

        Vector location = VectorUtils.any(hitCells);
        for (Vector coordinate : hitCells) {
            if (coordinate.x < location.x) {
                location = coordinate;
            }
            if (coordinate.y < location.y) {
                location = coordinate;
            }
        }

        return location;
    }

    /**
     * Finds {@link Cell#HIT} that do not host any ship.
     * Assumption is that the cells will belong to the same ship.
     */
    @NonNull
    public static Collection<Vector> findHitCellsWithNoShip(@NonNull Board board) {
        Collection<Vector> hitCells = new ArrayList<>();
        for (int i = 0; i < board.width(); i++) {
            for (int j = 0; j < board.height(); j++) {
                if (board.getCell(i, j) == Cell.HIT) {
                    if (!board.hasShipAt(i, j)) {
                        hitCells.add(Vector.get(i, j));
                    }
                }
            }
        }
        return hitCells;
    }

    @NonNull
    static String debugBoard(@NonNull Board board) {
        return board.toString();
    }
}
