package com.ivygames.morskoiboi.achievement;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import org.acra.ACRA;
import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.util.Collection;

public final class AchievementsManager {

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

    private final GoogleApiClient mApiClient;
    private final Tracker mGaTracker;
    private final GameSettings mSettings = GameSettings.get();

    private final AchievementsResultCallback mAchievementsLoadCallback;

    public AchievementsManager(GoogleApiClient apiClient, Tracker tracker) {
        Validate.notNull(tracker);
        mGaTracker = tracker;

        Validate.notNull(apiClient);
        mApiClient = apiClient;
        mAchievementsLoadCallback = new AchievementsResultCallback(apiClient);
    }

    public void loadAchievements() {
        PendingResult<LoadAchievementsResult> loadResult = Games.Achievements.load(mApiClient, true);
        loadResult.setResultCallback(mAchievementsLoadCallback);
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

    public void processAchievements(Game game, Collection<Ship> ships) {
        Ln.v("game: " + game + "; ships: " + ships);

        processCombo(game.getCombo());
        processShellsLeft(game.getShells());
        processTimeSpent(game.getTimeSpent());
        processShipsLeft(ships);

        if (RulesFactory.getRules().calcTotalScores(ships, game) >= 15000) {
            if (mSettings.isAchievementUnlocked(MILITARY_ACHIEVEMENTS)) {
                Ln.v(AchievementsManager.name(MILITARY_ACHIEVEMENTS) + " is already unlocked - do not increment");
            } else {
                increment(MILITARY_ACHIEVEMENTS, 1);
            }
        }
    }

    private void processCombo(int combo) {
        Ln.v("combo=" + combo);
        if (combo >= 1) {
            if (unlockIfNotUnlocked(NAVAL_MERIT)) {
                reveal(ORDER_OF_HONOUR);
            } else if (combo >= 2) {
                unlockIfNotUnlocked(ORDER_OF_HONOUR);
            }
        }
    }

    private void processShellsLeft(int shells) {
        Ln.v("shells=" + shells);
        if (shells >= 50) {
            if (unlockIfNotUnlocked(BRAVERY_AND_COURAGE)) {
                reveal(EXTRA_BRAVERY_AND_COURAGE);
            } else if (shells >= 60) {
                unlockIfNotUnlocked(EXTRA_BRAVERY_AND_COURAGE);
            }
        }
    }

    private void processTimeSpent(long time) {
        Ln.v("time=" + time);
        if (time <= 60000) {
            if (unlockIfNotUnlocked(FLYING_DUTCHMAN)) {
                // TODO: reveal next
            } else if (time < 50000) {
                // TODO: unlock next
            }
        }
    }

    private void processShipsLeft(Collection<Ship> ships) {
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
        mGaTracker.send(new AnalyticsEvent("achievement", achievementId).build());
        if (mApiClient.isConnected()) {
            Games.Achievements.unlock(mApiClient, achievementId);
        }
    }

    private void increment(String achievementId, int steps) {
        Ln.d("incrementing achievement: " + AchievementsManager.name(achievementId) + " by " + steps);
        if (mApiClient.isConnected()) {
            Games.Achievements.increment(mApiClient, achievementId, steps);
        }
    }

    private void reveal(String achievementId) {
        Ln.d("revealing achievement: " + AchievementsManager.name(achievementId));
        AchievementsUtils.setRevealed(achievementId);
        if (mApiClient.isConnected()) {
            Games.Achievements.reveal(mApiClient, achievementId);
        }
    }

    private static String name(String achievementId) {
        return AchievementsUtils.debugName(achievementId);
    }

}
