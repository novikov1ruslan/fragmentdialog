package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

class AchievementsUtils {
    private AchievementsUtils() {

    }

    static void setRevealed(@NonNull String achievementId, @NonNull AchievementsSettings settings) {
        if (isRevealed(achievementId, settings)) {
            Ln.v(achievementId + " already revealed");
        } else {
            settings.revealAchievement(achievementId);
        }
    }

    static boolean isRevealed(@NonNull String achievementId, @NonNull AchievementsSettings settings) {
        return settings.isAchievementRevealed(achievementId);
    }

    static void setUnlocked(@NonNull String achievementId, @NonNull AchievementsSettings settings) {
        if (isUnlocked(achievementId, settings)) {
            Ln.v(achievementId + " already unlocked");
        } else {
            settings.unlockAchievement(achievementId);
        }
    }

    static boolean isUnlocked(@NonNull String achievementId, @NonNull AchievementsSettings settings) {
        return settings.isAchievementUnlocked(achievementId);
    }

}
