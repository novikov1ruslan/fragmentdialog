package com.ivygames.morskoiboi.achievement;

import android.support.annotation.NonNull;

import com.ivygames.common.achievements.AchievementsManagerBase;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.model.Ship;

import org.commons.logger.Ln;

import java.util.Collection;

public class AchievementsManager extends AchievementsManagerBase {

    // achievements
    static final String NAVAL_MERIT = "CgkI8s_j3LsfEAIQAQ";
    static final String ORDER_OF_HONOUR = "CgkI8s_j3LsfEAIQGg";
    static final String BRAVERY_AND_COURAGE = "CgkI8s_j3LsfEAIQAg";
    static final String EXTRA_BRAVERY_AND_COURAGE = "CgkI8s_j3LsfEAIQGw";
    static final String FLYING_DUTCHMAN = "CgkI8s_j3LsfEAIQFw";
    static final String STEALTH = "CgkI8s_j3LsfEAIQAw";
    static final String LIFE_SAVING = "CgkI8s_j3LsfEAIQHA";
    /**
     * Win the battle with your aircraft carrier still afloat.
     */
    static final String AIRCRAFTSMAN = "CgkI8s_j3LsfEAIQBA";
    /**
     * Win the battle with both of your Cruisers afloat.
     */
    static final String CRUISER_COMMANDER = "CgkI8s_j3LsfEAIQDw";
    static final String DESTROYER = "CgkI8s_j3LsfEAIQBQ";
    static final String MILITARY_ACHIEVEMENTS = "CgkI8s_j3LsfEAIQEA";

    public static final int NORMAL_DIFFICULTY_PROGRESS_FACTOR = 1;

    public AchievementsManager(@NonNull ApiClient apiClient, @NonNull GameSettings settings) {
        super(settings, apiClient);
    }

    public void processScores(int scores) {
        Ln.v("scores: " + scores);
        if (scores >= 15000) {
            if (mSettings.isAchievementUnlocked(MILITARY_ACHIEVEMENTS)) {
                Ln.v(MILITARY_ACHIEVEMENTS + " is already unlocked - do not increment");
            } else {
                increment(MILITARY_ACHIEVEMENTS, 1);
            }
        }
    }

    public void processCombo(int combo) {
        Ln.v("combo=" + combo);
        if (combo >= 1) {
            if (unlockIfNotUnlocked(NAVAL_MERIT)) {
                reveal(ORDER_OF_HONOUR);
            } else if (combo >= 2) {
                unlockIfNotUnlocked(ORDER_OF_HONOUR);
            }
        }
    }

    public void processShellsLeft(int shells) {
        Ln.v("shells=" + shells);
        if (shells >= 50) {
            if (unlockIfNotUnlocked(BRAVERY_AND_COURAGE)) {
                reveal(EXTRA_BRAVERY_AND_COURAGE);
            } else if (shells >= 60) {
                unlockIfNotUnlocked(EXTRA_BRAVERY_AND_COURAGE);
            }
        }
    }

    public void processTimeSpent(long time) {
        Ln.v("time=" + time);
        if (time <= 60000) {
            if (unlockIfNotUnlocked(FLYING_DUTCHMAN)) {
                // TODO: reveal next
            } else if (time < 50000) {
                // TODO: unlock next
            }
        }
    }

    public void processShipsLeft(Collection<Ship> ships) {
        Ln.v("ships=" + ships);

        int shipsLeft = AchievementsManager.countAliveShips(ships);
        if (shipsLeft >= 3) {
            if (unlockIfNotUnlocked(STEALTH)) {
                reveal(LIFE_SAVING);
            } else if (shipsLeft >= 4) {
                unlockIfNotUnlocked(LIFE_SAVING);
            }
        }

        int cruisersCounter = 0;
        int destroyersCounter = 0;
        for (Ship ship : ships) {
            if (ship.getSize() == 4 && !ship.isDead()) {
                unlockIfNotUnlocked(AIRCRAFTSMAN);
            } else if (ship.getSize() == 3 && !ship.isDead()) {
                if (cruisersCounter == 1) {
                    unlockIfNotUnlocked(CRUISER_COMMANDER);
                }
                cruisersCounter++;
            } else if (ship.getSize() == 2 && !ship.isDead()) {
                if (destroyersCounter == 2) {
                    unlockIfNotUnlocked(DESTROYER);
                }
                destroyersCounter++;
            }
        }
    }

    private static int countAliveShips(Collection<Ship> ships) {
        int left = 0;
        for (Ship ship : ships) {
            if (!ship.isDead()) {
                left++;
            }
        }
        return left;
    }

    static String debugName(String id) {
        if (AchievementsManager.NAVAL_MERIT.equals(id)) {
            return "NAVAL_MERIT";
        } else if (AchievementsManager.ORDER_OF_HONOUR.equals(id)) {
            return "ORDER_OF_HONOUR";
        } else if (AchievementsManager.BRAVERY_AND_COURAGE.equals(id)) {
            return "BRAVERY_AND_COURAGE";
        } else if (AchievementsManager.EXTRA_BRAVERY_AND_COURAGE.equals(id)) {
            return "EXTRA_BRAVERY_AND_COURAGE";
        } else if (AchievementsManager.FLYING_DUTCHMAN.equals(id)) {
            return "FLYING_DUTCHMAN";
        } else if (AchievementsManager.STEALTH.equals(id)) {
            return "STEALTH";
        } else if (AchievementsManager.LIFE_SAVING.equals(id)) {
            return "LIFE_SAVING";
        } else if (AchievementsManager.AIRCRAFTSMAN.equals(id)) {
            return "AIRCRAFTSMAN";
        } else if (AchievementsManager.CRUISER_COMMANDER.equals(id)) {
            return "CRUISER_COMMANDER";
        } else if (AchievementsManager.DESTROYER.equals(id)) {
            return "DESTROYER";
        } else if (AchievementsManager.MILITARY_ACHIEVEMENTS.equals(id)) {
            return "MILITARY_ACHIEVEMENTS";
        }

        return "UNKNOWN(" + id + ")";
    }
}
