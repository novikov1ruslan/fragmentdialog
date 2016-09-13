package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.achievement.Achievements;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.googleapi.ApiClient;

import org.commons.logger.Ln;

public class AchievementsApi {
    @NonNull
    protected final AchievementsSettings mSettings;
    @NonNull
    protected final ApiClient mApiClient;
    @NonNull
    protected final AchievementsResultCallback mAchievementsLoadCallback;

    public AchievementsApi(@NonNull AchievementsSettings settings,
                           @NonNull ApiClient apiClient) {
        mSettings = new AchievementsSettingsWrapper(settings);
        mApiClient = apiClient;
        mAchievementsLoadCallback = new AchievementsResultCallback(apiClient, settings);
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
            mApiClient.unlock(achievementId);
        }
    }

    public void loadAchievements() {
        PendingResult<Achievements.LoadAchievementsResult> loadResult = mApiClient.load(true);
        loadResult.setResultCallback(mAchievementsLoadCallback);
    }

    public final void increment(@NonNull String achievementId, int steps) {
        Ln.d("incrementing achievement: " + achievementId + " by " + steps);
        if (mApiClient.isConnected()) {
            mApiClient.increment(achievementId, steps);
        }
    }

    public final void reveal(@NonNull String achievementId) {
        Ln.d("revealing achievement: " + achievementId);
        mSettings.revealAchievement(achievementId);
        if (mApiClient.isConnected()) {
            mApiClient.reveal(achievementId);
        }
    }
}
