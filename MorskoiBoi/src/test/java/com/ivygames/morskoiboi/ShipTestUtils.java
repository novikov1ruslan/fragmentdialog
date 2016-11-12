package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Ship;

public class ShipTestUtils {
    public static boolean isInProximity(Ship ship, int i, int j) {
        boolean isHorizontal = ship.isHorizontal();
        int x = ship.getX();
        int y = ship.getY();

        if (isHorizontal) {
            return i >= x - 1 && i <= x + ship.getSize() && j >= y - 1 && j <= y + 1;
        } else {
            return i >= x - 1 && i <= x + 1 && j >= y - 1 && j <= y + ship.getSize();
        }
    }
}
