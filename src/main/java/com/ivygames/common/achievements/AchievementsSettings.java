package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

public interface AchievementsSettings {
    boolean isAchievementUnlocked(@NonNull String achievementId);

    void unlockAchievement(@NonNull String achievementId);

    void revealAchievement(@NonNull String achievementId);

    boolean isAchievementRevealed(@NonNull String achievementId);
}
