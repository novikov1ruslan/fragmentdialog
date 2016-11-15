package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardSetupUtils {
    private BoardSetupUtils() {
    }

    static boolean onlyHorizontalShips(@NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            if (ship.getSize() > 1 && !ship.isHorizontal()) {
                return false;
            }
        }

        return true;
    }

    public static Collection<Vector2> getNeighboringCoordinates(int x, int y) {
        Ship ship = new Ship(1);
        ship.setCoordinates(x, y);

        return getCells(ship, true);
    }

    public static Collection<Vector2> getCells(@NonNull Ship ship, boolean neighboring) {
        Collection<Vector2> coordinates = new ArrayList<>();

        int x = ship.getX();
        int y = ship.getY();
        boolean horizontal = ship.isHorizontal();

        for (int i = -1; i <= ship.getSize(); i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                if (Board.contains(cellX, cellY)) {
                    boolean inShip = ship.isInShip(cellX, cellY);
                    if (inShip && !neighboring) {
                        coordinates.add(Vector2.get(cellX, cellY));
                    } else if (!inShip && neighboring) {
                        coordinates.add(Vector2.get(cellX, cellY));
                    }
                }
            }
        }

        return coordinates;
    }

    public static List<Vector2> getCellsFreeFromShips(@NonNull Board board, boolean allowAdjacentShips) {
        List<Vector2> cells = Vector2.getAllCoordinates();
        Collection<Ship> ships = board.getShips();
        for (Ship ship : ships) {
            cells.removeAll(getCells(ship, false));
            if (!allowAdjacentShips) {
                cells.removeAll(getCells(ship, true));
            }
        }
        return cells;
    }

}
