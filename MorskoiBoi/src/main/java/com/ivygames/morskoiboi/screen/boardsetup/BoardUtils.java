package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.LocatedShip;
import com.ivygames.battleship.board.Vector2;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ShipUtils;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardUtils {
    private BoardUtils() {
    }

    static boolean onlyHorizontalShips(@NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            if (ship.size > 1 && !ship.isHorizontal()) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    public static List<Vector2> getNeighboringCoordinates(int x, int y) {
        return getCells(new LocatedShip(new Ship(1), Vector2.get(x, y)), true);
    }

    // TODO: use enum instead of boolean
    @NonNull
    public static List<Vector2> getCells(@NonNull LocatedShip locatedShip, boolean neighboring) {
        List<Vector2> coordinates = new ArrayList<>();

        int x = locatedShip.position.x;
        int y = locatedShip.position.y;
        Ship ship = locatedShip.ship;
        boolean horizontal = ship.isHorizontal();

        for (int i = -1; i <= ship.size; i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                Vector2 v = Vector2.get(cellX, cellY);
                if (Board.contains(v)) {
                    boolean inShip = ShipUtils.isInShip(v, locatedShip);
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
        for (LocatedShip locatedShip : board.getLocatedShips()) {
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

    // TODO: when ship has no coordinates this method is not needed, use Board#addShip
    public static void putShipAt(@NonNull Board board, @NonNull LocatedShip locatedShip) {
        int i = locatedShip.position.x;
        int j = locatedShip.position.y;
        if (!board.shipFitsTheBoard(locatedShip.ship, i, j)) {
            throw new IllegalArgumentException("cannot put ship " + locatedShip);
        }

//        Collection<Vector2> neighboringCells = BoardUtils.getCells(ship, true);
//        for (Vector2 v : neighboringCells) {
//            if (!mRules.allowAdjacentShips()) {
//                if (ship.isDead()) {
//                    board.setCell(Cell.MISS, v);
//                } else {
//                    board.setCell(Cell.RESERVED, v);
//                }
//            }
//        }

        board.addShip(locatedShip);
    }

    @Nullable
    public static Ship pickShipFromBoard(@NonNull Board board, int i, int j) {
        if (!Board.contains(i, j)) {
            Ln.w("(" + i + "," + j + ") is outside the board");
            return null;
        }

        LocatedShip locatedShip = board.getFirstShipAt(i, j);
        if (locatedShip != null) {
            board.removeShip(locatedShip);
            return locatedShip.ship;
        }
        return null;
    }

    public static void rotateShipAt(@NonNull Board board, int x, int y) {
        if (!Board.contains(x, y)) {
            Ln.w("(" + x + "," + y + ") is outside the board");
            return;
        }

        Ship ship = pickShipFromBoard(board, x, y);
        if (ship == null) {
            return;
        }

        ship.rotate();

        if (board.shipFitsTheBoard(ship, x, y)) {
            putShipAt(board, new LocatedShip(ship, x, y));
        } else {
            int i = board.horizontalDimension() - ship.size;
            if (ship.isHorizontal()) {
                putShipAt(board, new LocatedShip(ship, i, y));
            } else {
                putShipAt(board, new LocatedShip(ship, x, i));
            }
        }
    }
}
