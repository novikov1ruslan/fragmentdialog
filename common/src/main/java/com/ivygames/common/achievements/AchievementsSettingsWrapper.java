package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

class AchievementsSettingsWrapper implements AchievementsSettings {
    @NonNull
    private final AchievementsSettings mSettings;

    AchievementsSettingsWrapper(@NonNull AchievementsSettings settings) {
        mSettings = settings;
    }

    @Override
    public boolean isAchievementUnlocked(@NonNull String achievementId) {
        return mSettings.isAchievementUnlocked(achievementId);
    }

    @Override
    public void unlockAchievement(@NonNull String achievementId) {
        if (isAchievementUnlocked(achievementId)) {
            Ln.v(achievementId + " already unlocked");
        } else {
            mSettings.unlockAchievement(achievementId);
        }
    }

    @Override
    public void revealAchievement(@NonNull String achievementId) {
        if (isAchievementRevealed(achievementId)) {
            Ln.v(achievementId + " already revealed");
        } else {
            mSettings.revealAchievement(achievementId);
        }
    }

    @Override
    public boolean isAchievementRevealed(@NonNull String achievementId) {
        return mSettings.isAchievementRevealed(achievementId);
    }
}
