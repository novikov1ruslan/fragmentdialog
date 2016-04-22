package com.ivygames.morskoiboi.utils;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.List;
import java.util.Locale;

public final class GameUtils {

    public static final int PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL = 2;

    private GameUtils() {
        // utility
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

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        return String.format(Locale.US, "%d:%02d", minutes, seconds % 60);
    }
}
