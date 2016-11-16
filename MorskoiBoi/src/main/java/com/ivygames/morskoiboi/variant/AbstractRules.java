package com.ivygames.morskoiboi.variant;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupUtils;

import java.util.Collection;

public abstract class AbstractRules implements Rules {

    @Override
    public int calcSurrenderPenalty(@NonNull Collection<Ship> ships) {
        int decksLost = getTotalHealth(getAllShipsSizes()) - getRemainedHealth(ships);
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
