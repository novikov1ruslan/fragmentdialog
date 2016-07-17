package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public class BoardSetupUtils {
    private BoardSetupUtils() {}

    static boolean onlyHorizontalShips(@NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            if (ship.getSize() > 1 && !ship.isHorizontal()) {
                return false;
            }
        }

        return true;
    }
}
