package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.ivygames.common.achievements.AchievementsSettings;
import com.ivygames.common.achievements.AchievementsUtils;
import com.ivygames.common.googleapi.ApiClient;

import org.commons.logger.Ln;

public final class AchievementsResultCallback implements ResultCallback<Achievements.LoadAchievementsResult> {

    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final AchievementsSettings mSettings;

    AchievementsResultCallback(@NonNull ApiClient apiClient, @NonNull AchievementsSettings settings) {
        mApiClient = apiClient;
        mSettings = settings;
    }

    @Override
    public void onResult(@NonNull LoadAchievementsResult result) {
        int statusCode = result.getStatus().getStatusCode();
        if (achievementsLoaded(statusCode)) {
            AchievementBuffer buffer = result.getAchievements();
            Ln.d(buffer.getCount() + " achievements loaded");
            for (int i = 0; i < buffer.getCount(); i++) {
                processAchievement(buffer.get(i));
            }
            buffer.release();
        } else {
            Ln.w("achievements loading failed");
        }
    }

    private void processAchievement(Achievement achievement) {
        Ln.v("processing achievement: " + achievement.getName());
        String achievementId = achievement.getAchievementId();
        if (achievement.getState() == Achievement.STATE_UNLOCKED) {
            AchievementsUtils.setUnlocked(achievementId, mSettings);
        } else if (achievement.getState() == Achievement.STATE_REVEALED) {
            if (AchievementsUtils.isUnlocked(achievementId, mSettings)) {
                Ln.d("[" + achievement.getName() + "] was unlocked but not posted - posting to the cloud");
                mApiClient.unlock(achievementId);
            } else {
                AchievementsUtils.setRevealed(achievementId, mSettings);
            }
        } else if (AchievementsUtils.isUnlocked(achievementId, mSettings)) {
            Ln.d("[" + achievement.getName() + "] was unlocked but not posted - posting to the cloud");
            mApiClient.unlock(achievementId);
        } else if (AchievementsUtils.isRevealed(achievementId, mSettings)) {
            Ln.d("[" + achievement.getName() + "] was revealed but not posted - posting to the cloud");
            mApiClient.reveal(achievementId);
        }
    }

    private boolean achievementsLoaded(int statusCode) {
        return statusCode == GamesStatusCodes.STATUS_OK || statusCode == GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA;
    }

}
