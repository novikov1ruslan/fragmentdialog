package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.achievement.Achievements;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.analytics.AnalyticsEvent;

import org.commons.logger.Ln;

public class AchievementsManagerBase {
    @NonNull
    protected final AchievementsSettings mSettings;
    @NonNull
    protected final ApiClient mApiClient;
    @NonNull
    protected final AchievementsResultCallback mAchievementsLoadCallback;

    public AchievementsManagerBase(@NonNull AchievementsSettings settings, @NonNull ApiClient apiClient) {
        mSettings = settings;
        mApiClient = apiClient;
        mAchievementsLoadCallback = new AchievementsResultCallback(apiClient, settings);
    }

    /**
     * @return true if change has been made
     */
    protected boolean unlockIfNotUnlocked(String achievementId) {
        boolean alreadyUnlocked = mSettings.isAchievementUnlocked(achievementId);
        if (alreadyUnlocked) {
            Ln.d(achievementId + " already unlocked - no need to unlock");
            return false;
        } else {
            unlock(achievementId);
            return true;
        }
    }

    private void unlock(String achievementId) {
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

    protected final void increment(String achievementId, int steps) {
        Ln.d("incrementing achievement: " + achievementId + " by " + steps);
        if (mApiClient.isConnected()) {
            mApiClient.increment(achievementId, steps);
        }
    }

    protected final void reveal(String achievementId) {
        Ln.d("revealing achievement: " + achievementId);
        AchievementsUtils.setRevealed(achievementId, mSettings);
        if (mApiClient.isConnected()) {
            mApiClient.reveal(achievementId);
        }
    }
}
