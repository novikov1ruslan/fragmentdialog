package com.ivygames;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShipTestUtils {

    @NonNull
    public static Ship mockDeadShip() {
        Ship ship = mock(Ship.class);
        when(ship.isDead()).thenReturn(true);
        return ship;
    }
}
