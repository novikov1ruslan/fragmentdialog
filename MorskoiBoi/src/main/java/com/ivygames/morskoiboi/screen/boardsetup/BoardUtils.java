package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardUtils {
    private BoardUtils() {
    }

    static boolean onlyHorizontalShips(@NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            if (ship.getSize() > 1 && !ship.isHorizontal()) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    public static List<Vector2> getNeighboringCoordinates(int x, int y) {
        return getCells(new Board.LocatedShip(new Ship(1), Vector2.get(x, y)), true);
    }

    // TODO: use enum instead of boolean
    @NonNull
    private static List<Vector2> getCells(@NonNull Board.LocatedShip locatedShip, boolean neighboring) {
        List<Vector2> coordinates = new ArrayList<>();

        int x = locatedShip.position.x;
        int y = locatedShip.position.y;
        Ship ship = locatedShip.ship;
        boolean horizontal = ship.isHorizontal();

        for (int i = -1; i <= ship.getSize(); i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                Vector2 v = Vector2.get(cellX, cellY);
                if (Board.contains(v)) {
                    boolean inShip = Ship.isInShip(v, locatedShip);
                    if (inShip && !neighboring) {
                        coordinates.add(v);
                    } else if (!inShip && neighboring) {
                        coordinates.add(v);
                    }
                }
            }
        }

        return coordinates;
    }

    public static List<Vector2> getCellsFreeFromShips(@NonNull Board board, boolean allowAdjacentShips) {
        List<Vector2> cells = Vector2.getAllCoordinates();
        for (Board.LocatedShip locatedShip : board.getLocatedShips()) {
            cells.removeAll(getCells(locatedShip, false));
            if (!allowAdjacentShips) {
                cells.removeAll(getCells(locatedShip, true));
            }
        }
        return cells;
    }

    public static List<Vector2> getPossibleShots(@NonNull Board board, boolean allowAdjacentShips) {
        List<Vector2> cells = getCellsFreeFromShips(board, allowAdjacentShips);
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

            Collection<Vector2> coordinates = getNeighboringCoordinates(i, j);
            for (Vector2 v : coordinates) {
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
        return allShipsAreOnBoard(board, rules) && !hasConflictingCell(board, rules.allowAdjacentShips());
    }

    private static boolean allShipsAreOnBoard(@NonNull Board board, @NonNull Rules rules) {
        return board.getShips().size() == rules.getAllShipsSizes().length;
    }

    public static boolean hasConflictingCell(@NonNull Board board, boolean allowAdjacentShips) {
        for (int i = 0; i < board.horizontalDimension(); i++) {
            for (int j = 0; j < board.verticalDimension(); j++) {
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
    public static boolean isItDefeatedBoard(@NonNull Board board, @NonNull Rules rules) {
        return allShipsAreOnBoard(board, rules) && Board.allAvailableShipsAreDestroyed(board);
    }

}
