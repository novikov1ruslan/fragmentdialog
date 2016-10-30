package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.googleapi.ApiClient;

import org.commons.logger.Ln;

public class AchievementsApi {
    private static final String API_NOT_CONNECTED = "api_not_connected_";

    @NonNull
    private final AchievementsSettings mSettings;
    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final AchievementsResultCallback mAchievementsLoadCallback;

    public AchievementsApi(@NonNull AchievementsSettings settings,
                           @NonNull ApiClient apiClient) {
        mSettings = new AchievementsSettingsWrapper(settings);
        mApiClient = apiClient;
        AchievementsLoadListener listener = new AchievementsLoadListener(apiClient, mSettings);
        mAchievementsLoadCallback = new AchievementsResultCallback(listener);
    }

    /**
     * @return true if change has been made
     */
    public boolean unlockIfNotUnlocked(@NonNull String achievementId) {
        if (mSettings.isAchievementUnlocked(achievementId)) {
            Ln.d(achievementId + " already unlocked - no need to unlock");
            return false;
        } else {
            unlock(achievementId);
            return true;
        }
    }

    private void unlock(@NonNull String achievementId) {
        Ln.d("unlocking achievement: " + achievementId);
        mSettings.unlockAchievement(achievementId);
        AnalyticsEvent.send("achievement", achievementId);
        if (mApiClient.isConnected()) {
            mApiClient.unlockAchievement(achievementId);
        } else {
            Ln.e(API_NOT_CONNECTED + "unlock");
        }
    }

    public void loadAchievements() {
        if (mApiClient.isConnected()) {
            mApiClient.loadAchievements(mAchievementsLoadCallback);
        } else {
            Ln.e(API_NOT_CONNECTED + "loadAchievements");
        }
    }

    public final void increment(@NonNull String achievementId, int steps) {
        Ln.d("incrementing achievement: " + achievementId + " by " + steps);
        if (mApiClient.isConnected()) {
            mApiClient.increment(achievementId, steps);
        } else {
            Ln.e(API_NOT_CONNECTED + "increment");
        }
    }

    public final void reveal(@NonNull String achievementId) {
        Ln.d("revealing achievement: " + achievementId);
        mSettings.revealAchievement(achievementId);
        if (mApiClient.isConnected()) {
            mApiClient.revealAchievement(achievementId);
        } else {
            Ln.e(API_NOT_CONNECTED + "reveal");
        }
    }
}
