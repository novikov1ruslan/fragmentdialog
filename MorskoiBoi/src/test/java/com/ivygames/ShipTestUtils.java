package com.ivygames;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

public class ShipTestUtils {

    @NonNull
    public static Ship deadShip() {
        Ship ship = new Ship(1);
        ship.shoot();
        return ship;
    }
}
