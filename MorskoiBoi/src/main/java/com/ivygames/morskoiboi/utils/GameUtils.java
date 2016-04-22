package com.ivygames.morskoiboi.utils;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class GameUtils {

    public static final int PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL = 2;
    private static volatile long sRandomCounter;

    private GameUtils() {
        // utility
    }

    /**
     * @return true if coordinates {@code vector} are aligned horizontally
     */
    public static boolean coordinatesAlignedHorizontally(List<Vector2> coordinates) {

        int y = coordinates.get(0).getY();

        for (int i = 1; i < coordinates.size(); i++) {
            if (y != coordinates.get(i).getY()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return true if the {@code board} has empty space for the {@code ship} at coordinates ({@code i},{@code j}
     */
    public static boolean isPlaceEmpty(@NonNull Ship ship, @NonNull Board board, int i, int j) {
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

    @NonNull
    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        return String.format(Locale.US, "%d:%02d", minutes, seconds % 60);
    }

    @NonNull
    public static LinkedList<Ship> getWorkingShips(Collection<Ship> ships) {
        LinkedList<Ship> workingShips = new LinkedList<>();
        for (Ship ship : ships) {
            if (!ship.isDead()) {
                workingShips.add(ship);
            }
        }
        return workingShips;
    }

    public static void removeShipFromFleet(Collection<Ship> fleet, Ship ship) {
        Iterator<Ship> iterator = fleet.iterator();
        while (iterator.hasNext()) {
            Ship next = iterator.next();
            if (ship.getSize() == next.getSize()) {
                iterator.remove();
                break;
            }
        }
    }

    @NonNull
    public static Collection<Ship> generateShipsForSizes(int[] allShipsSizes) {
        Random random = new Random(System.currentTimeMillis() + ++sRandomCounter);

        List<Ship> fleet = new ArrayList<>();
        for (int length : allShipsSizes) {
            fleet.add(new Ship(length, calcRandomOrientation(random)));
        }

        return fleet;
    }

    private static Ship.Orientation calcRandomOrientation(Random random) {
        return random.nextInt(2) == 1 ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
    }
}
