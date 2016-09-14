package com.ivygames.common.achievements;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.google.android.gms.games.achievement.Achievement;
import com.ivygames.common.googleapi.ApiClient;

import org.commons.logger.Ln;

import java.util.List;

final class AchievementsLoadListener {

    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final AchievementsSettings mSettings;

    AchievementsLoadListener(@NonNull ApiClient apiClient, @NonNull AchievementsSettings settings) {
        mApiClient = apiClient;
        mSettings = new AchievementsSettingsWrapper(settings);
    }

    void onAchievementsLoaded(@NonNull @Size(min = 1) List<GameAchievement> achievements) {
        for (GameAchievement achievement : achievements) {
            processAchievement(achievement);
        }
    }

    private void processAchievement(@NonNull GameAchievement achievement) {
        Ln.v("processing achievement: " + achievement.name);
        if (achievement.state == Achievement.STATE_UNLOCKED) {
            mSettings.unlockAchievement(achievement.id);
        } else if (achievement.state == Achievement.STATE_REVEALED) {
            if (mSettings.isAchievementUnlocked(achievement.id)) {
                Ln.d("[" + achievement.name + "] was unlocked but not posted - posting to the cloud");
                mApiClient.unlockAchievement(achievement.id);
            } else {
                mSettings.revealAchievement(achievement.id);
            }
        } else if (mSettings.isAchievementUnlocked(achievement.id)) {
            Ln.d("[" + achievement.name + "] was unlocked but not posted - posting to the cloud");
            mApiClient.unlockAchievement(achievement.id);
        } else if (mSettings.isAchievementRevealed(achievement.id)) {
            Ln.d("[" + achievement.name + "] was revealed but not posted - posting to the cloud");
            mApiClient.revealAchievement(achievement.id);
        }
    }
}
