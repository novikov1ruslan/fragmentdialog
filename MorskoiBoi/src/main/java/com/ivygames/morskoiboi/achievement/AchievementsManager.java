package com.ivygames.morskoiboi.achievement;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.model.Ship;

import org.commons.logger.Ln;

import java.util.Collection;

public class AchievementsManager {

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

    @NonNull
    private final GoogleApiClientWrapper mApiClient;
    @NonNull
    private final GameSettings mSettings;
    @NonNull
    private final AchievementsResultCallback mAchievementsLoadCallback;

    public AchievementsManager(@NonNull GoogleApiClientWrapper apiClient, @NonNull GameSettings settings) {
        mApiClient = apiClient;
        mSettings = settings;
        mAchievementsLoadCallback = new AchievementsResultCallback(apiClient, settings);
    }

    public void loadAchievements() {
        PendingResult<LoadAchievementsResult> loadResult = mApiClient.load(true);
        loadResult.setResultCallback(mAchievementsLoadCallback);
    }

    public void processScores(int scores) {
        Ln.v("scores: " + scores);
        if (scores >= 15000) {
            if (mSettings.isAchievementUnlocked(MILITARY_ACHIEVEMENTS)) {
                Ln.v(AchievementsManager.name(MILITARY_ACHIEVEMENTS) + " is already unlocked - do not increment");
            } else {
                increment(MILITARY_ACHIEVEMENTS, 1);
            }
        }
    }

    /**
     * @return true if change has been made
     */
    private boolean unlockIfNotUnlocked(String achievementId) {
        boolean alreadyUnlocked = mSettings.isAchievementUnlocked(achievementId);
        if (alreadyUnlocked) {
            Ln.d(AchievementsManager.name(achievementId) + " already unlocked - no need to unlock");
            return false;
        } else {
            unlock(achievementId);
            return true;
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

    private void unlock(String achievementId) {
        Ln.d("unlocking achievement: " + AchievementsManager.name(achievementId));
        mSettings.unlockAchievement(achievementId);
        AnalyticsEvent.send("achievement", achievementId);
        if (mApiClient.isConnected()) {
            mApiClient.unlock(achievementId);
        }
    }

    private void increment(String achievementId, int steps) {
        Ln.d("incrementing achievement: " + AchievementsManager.name(achievementId) + " by " + steps);
        if (mApiClient.isConnected()) {
            mApiClient.increment(achievementId, steps);
        }
    }

    private void reveal(String achievementId) {
        Ln.d("revealing achievement: " + AchievementsManager.name(achievementId));
        AchievementsUtils.setRevealed(achievementId, mSettings);
        if (mApiClient.isConnected()) {
            mApiClient.reveal(achievementId);
        }
    }

    private static String name(String achievementId) {
        return AchievementsUtils.debugName(achievementId);
    }

}
