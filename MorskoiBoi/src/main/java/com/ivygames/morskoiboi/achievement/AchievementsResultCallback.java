package com.ivygames.morskoiboi.achievement;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

final class AchievementsResultCallback implements ResultCallback<Achievements.LoadAchievementsResult> {

    @NonNull
    private final GoogleApiClientWrapper mApiClient;

    AchievementsResultCallback(@NonNull GoogleApiClientWrapper apiClient) {
        mApiClient = apiClient;
    }

    @Override
    public void onResult(LoadAchievementsResult result) {
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
            AchievementsUtils.setUnlocked(achievementId);
        } else if (achievement.getState() == Achievement.STATE_REVEALED) {
            if (AchievementsUtils.isUnlocked(achievementId)) {
                Ln.d("[" + achievement.getName() + "] was unlocked but not posted - posting to the cloud");
                mApiClient.unlock(achievementId);
            } else {
                AchievementsUtils.setRevealed(achievementId);
            }
        } else if (AchievementsUtils.isUnlocked(achievementId)) {
            Ln.d("[" + achievement.getName() + "] was unlocked but not posted - posting to the cloud");
            mApiClient.unlock(achievementId);
        } else if (AchievementsUtils.isRevealed(achievementId)) {
            Ln.d("[" + achievement.getName() + "] was revealed but not posted - posting to the cloud");
            mApiClient.reveal(achievementId);
        }
    }

    private boolean achievementsLoaded(int statusCode) {
        return statusCode == GamesStatusCodes.STATUS_OK || statusCode == GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA;
    }

}
