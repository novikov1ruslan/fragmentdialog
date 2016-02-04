package com.ivygames.morskoiboi.variant;

import android.graphics.Bitmap;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import org.commons.logger.Ln;

import java.util.Collection;

public class RussianRules extends AbstractRules {

    private static final int[] TOTAL_SHIPS = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

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

    @Override
    public boolean isCellConflicting(Cell cell) {
        return cell.getProximity() > Cell.RESERVED_PROXIMITY_VALUE;
    }

    @Override
    public int[] getTotalShips() {
        return TOTAL_SHIPS;
    }

    @Override
    public int calcTotalScores(Collection<Ship> ships, Game game) {
        float timeMultiplier = getTimeMultiplier(game.getTimeSpent());
        int shellsBonus = calcShellsBonus(game.getShells());
        int shipsBonus = calcSavedShipsBonus(ships);
        int comboBonus = calcComboBonus(game.getCombo());

        return (int) (shellsBonus * timeMultiplier) + shipsBonus + comboBonus;
    }

    private int calcComboBonus(int combo) {
        return combo * COMBO_UNIT_BONUS;
    }


    private int calcShellsBonus(int shells) {
        return shells * SHELL_UNIT_BONUS;
    }

    private float getTimeMultiplier(long millis) {
        if (millis >= MAX_TIME_MILLIS) {
            return MIN_TIME_BONUS_MULTIPLIER;
        }

        if (millis <= MIN_TIME_MILLIS) {
            return MAX_TIME_BONUS_MULTIPLIER;
        }

        return MIN_TIME_BONUS_MULTIPLIER + (float) (MAX_TIME_MILLIS - millis) / (float) (MAX_TIME_MILLIS - MIN_TIME_MILLIS);
    }

    private int calcSavedShipsBonus(Collection<Ship> ships) {

        int shipsWeight = 0;
        for (Ship ship : ships) {
            if (ship.isDead()) {
                continue;
            }

            switch (ship.getSize()) {
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
                    Ln.e("impossible ship size = " + ship.getSize());
                    break;
            }
        }
        return shipsWeight * SHIP_UNIT_BONUS;
    }

    @Override
    public Bitmap getBitmapForShipSize(int size) {
        Bitmaps bitmaps = Bitmaps.getInstance();
        switch (size) {
            case 4:
                return bitmaps.getBitmap(R.drawable.aircraft_carrier);
            case 3:
                return bitmaps.getBitmap(R.drawable.battleship);
            case 2:
                return bitmaps.getBitmap(R.drawable.frigate);
            case 1:
            default:
                return bitmaps.getBitmap(R.drawable.gunboat);
        }
    }

    @Override
    public int[] newShipTypesArray() {
        return new int[]{4, 3, 2, 1};
    }
}
