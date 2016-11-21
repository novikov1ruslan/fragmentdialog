package com.ivygames.morskoiboi.variant;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

import java.util.Collection;

public abstract class RulesUtils {

    private static final int SURRENDER_PENALTY_PER_DECK = 100;
    private static final int MIN_SURRENDER_PENALTY = 1000;

    public static int calcSurrenderPenalty(int[] allShipsSizes, @NonNull Collection<Ship> remainedShips) {
        int decksLost = getShipsHealth(allShipsSizes) - getShipsHealth(remainedShips);
        return decksLost * SURRENDER_PENALTY_PER_DECK + MIN_SURRENDER_PENALTY;
    }

    private static int getShipsHealth(int[] ships) {
        int totalHealth = 0;
        for (int ship : ships) {
            totalHealth += ship;
        }
        return totalHealth;
    }

    private static int getShipsHealth(@NonNull Collection<Ship> ships) {
        int health = 0;
        for (Ship ship : ships) {
            health += ship.getHealth();
        }
        return health;
    }
}
