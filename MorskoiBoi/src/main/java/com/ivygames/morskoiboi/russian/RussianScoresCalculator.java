package com.ivygames.morskoiboi.russian;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.ScoreStatistics;
import com.ivygames.morskoiboi.ScoresCalculator;

import org.commons.logger.Ln;

import java.util.Collection;

public class RussianScoresCalculator implements ScoresCalculator {
    private static final int MAX_TIME_MILLIS = 300000; // 5 minutes
    private static final int MIN_TIME_MILLIS = 20000; // 20 sec
    private static final int SHIP_1X_WEIGHT = 1;
    private static final int SHIP_2X_WEIGHT = 3;
    private static final int SHIP_3X_WEIGHT = 6;
    private static final int SHIP_4X_WEIGHT = 10;
    private static final int SHELL_UNIT_BONUS = 100;
    private static final int SHIP_UNIT_BONUS = 230;  // 80shells/35weights = 2.3
    private static final int COMBO_UNIT_BONUS = 800; // 8 * SHELL_UNIT_BONUS
    private static final float MAX_TIME_BONUS_MULTIPLIER = 2f;
    private static final float MIN_TIME_BONUS_MULTIPLIER = 1f;

    private static int calcComboBonus(int combo) {
        return combo * COMBO_UNIT_BONUS;
    }

    private static int calcShellsBonus(int shells) {
        return shells * SHELL_UNIT_BONUS;
    }

    private static float getTimeMultiplier(long millis) {
        if (millis >= MAX_TIME_MILLIS) {
            return MIN_TIME_BONUS_MULTIPLIER;
        }

        if (millis <= MIN_TIME_MILLIS) {
            return MAX_TIME_BONUS_MULTIPLIER;
        }

        return MIN_TIME_BONUS_MULTIPLIER + (float) (MAX_TIME_MILLIS - millis) / (float) (MAX_TIME_MILLIS - MIN_TIME_MILLIS);
    }

    private static int calcSavedShipsBonus(@NonNull Collection<Ship> ships) {

        int shipsWeight = 0;
        for (Ship ship : ships) {
            if (ship.isDead()) {
                continue;
            }

            switch (ship.size) {
                case 1:
                    shipsWeight += SHIP_1X_WEIGHT;
                    break;
                case 2:
                    shipsWeight += SHIP_2X_WEIGHT;
                    break;
                case 3:
                    shipsWeight += SHIP_3X_WEIGHT;
                    break;
                case 4:
                    shipsWeight += SHIP_4X_WEIGHT;
                    break;
                default:
                    Ln.e("impossible ship size = " + ship.size);
                    break;
            }
        }
        return shipsWeight * SHIP_UNIT_BONUS;
    }

    @Override
    public int calcScoresForAndroidGame(@NonNull Collection<Ship> ships, @NonNull ScoreStatistics statistics) {
        float timeMultiplier = getTimeMultiplier(statistics.getTimeSpent());
        int shellsBonus = calcShellsBonus(statistics.getShells());
        int shipsBonus = calcSavedShipsBonus(ships);
        int comboBonus = calcComboBonus(statistics.getCombo());

        return (int) (shellsBonus * timeMultiplier) + shipsBonus + comboBonus;
    }
}
