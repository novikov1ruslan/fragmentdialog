package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

import java.util.Collection;
import java.util.Iterator;

public class BoardTestUtils {
    public static boolean similar(Board board1, Board board2) {
        return similar(board1.mCells, board2.mCells) && similar(board1.getLocatedShips(), board2.getLocatedShips());
    }

    // TODO: remove when cell becomes immutable
    private static boolean similar(Cell[][] cells1, Cell[][] cells2) {
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

    private static boolean similar(@NonNull Collection<LocatedShip> ships1, @NonNull Collection<LocatedShip> ships2) {
        if (ships1.size() != ships2.size()) {
            return false;
        }

        Iterator<LocatedShip> iterator = ships2.iterator();
        for (LocatedShip locatedShip : ships1) {
            if (!similar(locatedShip, iterator.next())) {
                return false;
            }
        }

        return true;
    }

    private static boolean similar(LocatedShip ship1, LocatedShip ship2) {
        if (!similar(ship1.ship, ship2.ship)) {
            return false;
        }

        return ship1.position == ship2.position;
    }

    public static boolean similar(Ship ship1, Ship ship2) {
        if (ship1.isDead() && !ship1.isDead()) {
            return false;
        }
        if (ship1.isHorizontal() && !ship2.isHorizontal()) {
            return false;
        }

        return ship1.getHealth() == ship2.getHealth();
    }
}
