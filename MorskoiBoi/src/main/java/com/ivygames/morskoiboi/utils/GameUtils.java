package com.ivygames.morskoiboi.utils;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class GameUtils {

    public static final int PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL = 2;
    private static volatile long sRandomCounter;

    private GameUtils() {
        // utility
    }

    public static Collection<Ship> generateFullHorizontalFleet(@NonNull int[] totalShips) {
        ArrayList<Ship> ships = new ArrayList<>(totalShips.length);
        for (int i = totalShips.length - 1; i >= 0 ; i--) {
            ships.add(new Ship(totalShips[i]));
        }
        return ships;
    }

    /**
     * @return true if the cells are aligned horizontally
     */
    public static boolean areCellsHorizontal(List<Vector2> vector) {

        int y = vector.get(0).getY();

        for (int i = 1; i < vector.size(); i++) {
            if (y != vector.get(i).getY()) {
                return false;
            }
        }

        return true;
    }

    private static Ship.Orientation calcRandomOrientation(Random random) {
        return random.nextInt(2) == 1 ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
    }

    // TODO: do via priority queue
    public static List<Ship> generateFullFleet(int[] shipsLength) {
        Random random = new Random(System.currentTimeMillis() + ++sRandomCounter);

        // order is important
        List<Ship> fullSet = new ArrayList<>();
        for (int length : shipsLength) {
            fullSet.add(new Ship(length, calcRandomOrientation(random)));
        }

        return fullSet;
    }

    /**
     * @return true if the {@code board} has empty space for the {@code ship} at coordinates
     */
    public static boolean isPlaceEmpty(Ship ship, Board board, int i, int j) {
        boolean isHorizontal = ship.isHorizontal();
        for (int k = isHorizontal ? i : j; k < (isHorizontal ? i : j) + ship.getSize(); k++) {
            int x = isHorizontal ? k : i;
            int y = isHorizontal ? j : k;
            if (!board.getCell(x, y).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        return String.format(Locale.US, "%d:%02d", minutes, seconds % 60);
    }
}
