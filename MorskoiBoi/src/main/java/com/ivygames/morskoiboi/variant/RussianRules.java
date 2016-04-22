package com.ivygames.morskoiboi.variant;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.rt.InternetGame;

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
    private static final int MAX_SCORE_FOR_SURRENDERED_GAME = 5000;

    @NonNull
    private final Resources mResources;

    public RussianRules(@NonNull Resources resources) {
        mResources = resources;
    }

    @Override
    public boolean isCellConflicting(Cell cell) {
        return cell.isReserved() && cell.getProximity() > Cell.RESERVED_PROXIMITY_VALUE;
    }

    @Override
    public int[] getAllShipsSizes() {
        return TOTAL_SHIPS;
    }

    @Override
    public int calcTotalScores(@NonNull Collection<Ship> ships, @NonNull Game game, boolean surrendered) {
        int score = calculateScoresForGame(ships, game);

        if (surrendered) {
            score = score / 2;
            if (score > MAX_SCORE_FOR_SURRENDERED_GAME) {
                score = MAX_SCORE_FOR_SURRENDERED_GAME;
            }
        }
        return score;
    }

    private static int calculateScoresForGame(@NonNull Collection<Ship> ships, @NonNull Game game) {
        int progress;
        Game.Type type = game.getType();
        if (type == Game.Type.VS_ANDROID) {
            progress = calcScoresForAndroidGame(ships, game) * AchievementsManager.NORMAL_DIFFICULTY_PROGRESS_FACTOR;
        } else if (type == Game.Type.INTERNET) {
            progress = InternetGame.WIN_PROGRESS_POINTS;
        } else {
            progress = BluetoothGame.WIN_PROGRESS_POINTS;
        }
        return progress;
    }

    private static int calcScoresForAndroidGame(@NonNull Collection<Ship> ships, @NonNull Game game) {
        float timeMultiplier = getTimeMultiplier(game.getTimeSpent());
        int shellsBonus = calcShellsBonus(game.getShells());
        int shipsBonus = calcSavedShipsBonus(ships);
        int comboBonus = calcComboBonus(game.getCombo());

        return (int) (shellsBonus * timeMultiplier) + shipsBonus + comboBonus;
    }

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
        // TODO: remove: part of variants, but not part of rules
        Bitmaps bitmaps = Bitmaps.getInstance();
        switch (size) {
            case 4:
                return bitmaps.getBitmap(mResources, R.drawable.aircraft_carrier);
            case 3:
                return bitmaps.getBitmap(mResources, R.drawable.battleship);
            case 2:
                return bitmaps.getBitmap(mResources, R.drawable.frigate);
            case 1:
            default:
                return bitmaps.getBitmap(mResources, R.drawable.gunboat);
        }
    }

    @Override
    public Cell getAdjacentCellForShip(@NonNull Ship ship, @NonNull Cell cell) {
        if (ship.isDead()) {
            cell.setMiss();
        } else {
            cell.setReserved();
        }
        return cell;
    }
}
