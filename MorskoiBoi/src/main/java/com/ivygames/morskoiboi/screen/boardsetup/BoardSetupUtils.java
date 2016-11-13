package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.ArrayList;
import java.util.Collection;

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

    // TODO: unit test
    public static Collection<Vector2> getNeighboringCoordinates(int x, int y) {
        Collection<Vector2> coordinates = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j < 2; j++) {
                if ((i == 0) && (j == 0)) {
                    continue;
                }
                int cellX = x + i;
                int cellY = y + j;
                if (Board.contains(cellX, cellY)) {
                    coordinates.add(Vector2.get(cellX, cellY));
                }
            }
        }
        return coordinates;
    }
}
