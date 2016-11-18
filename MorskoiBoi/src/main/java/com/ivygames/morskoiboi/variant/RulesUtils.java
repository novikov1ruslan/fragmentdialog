package com.ivygames.morskoiboi.variant;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public abstract class RulesUtils {

    public static int calcSurrenderPenalty(@NonNull Collection<Ship> ships, int[] shipsSizes) {
        int decksLost = getTotalHealth(shipsSizes) - getRemainedHealth(ships);
        return decksLost * Game.SURRENDER_PENALTY_PER_DECK + Game.MIN_SURRENDER_PENALTY;
    }

    private static int getTotalHealth(int[] ships) {
        int totalHealth = 0;
        for (int ship : ships) {
            totalHealth += ship;
        }
        return totalHealth;
    }

    private static int getRemainedHealth(@NonNull Collection<Ship> ships) {
        int health = 0;
        for (Ship ship : ships) {
            health += ship.getHealth();
        }
        return health;
    }
}
