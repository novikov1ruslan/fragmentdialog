package com.ivygames.morskoiboi.achievement;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.GameSettings;

import org.commons.logger.Ln;

class AchievementsUtils {
    private AchievementsUtils() {

    }

    static String debugName(String id) {
        if (AchievementsManager.NAVAL_MERIT.equals(id)) {
            return "NAVAL_MERIT";
        } else if (AchievementsManager.ORDER_OF_HONOUR.equals(id)) {
            return "ORDER_OF_HONOUR";
        } else if (AchievementsManager.BRAVERY_AND_COURAGE.equals(id)) {
            return "BRAVERY_AND_COURAGE";
        } else if (AchievementsManager.EXTRA_BRAVERY_AND_COURAGE.equals(id)) {
            return "EXTRA_BRAVERY_AND_COURAGE";
        } else if (AchievementsManager.FLYING_DUTCHMAN.equals(id)) {
            return "FLYING_DUTCHMAN";
        } else if (AchievementsManager.STEALTH.equals(id)) {
            return "STEALTH";
        } else if (AchievementsManager.LIFE_SAVING.equals(id)) {
            return "LIFE_SAVING";
        } else if (AchievementsManager.AIRCRAFTSMAN.equals(id)) {
            return "AIRCRAFTSMAN";
        } else if (AchievementsManager.CRUISER_COMMANDER.equals(id)) {
            return "CRUISER_COMMANDER";
        } else if (AchievementsManager.DESTROYER.equals(id)) {
            return "DESTROYER";
        } else if (AchievementsManager.MILITARY_ACHIEVEMENTS.equals(id)) {
            return "MILITARY_ACHIEVEMENTS";
        }

        return "UNKNOWN(" + id + ")";
    }

    static void setUnlocked(String achievementId, @NonNull GameSettings settings) {
        if (isUnlocked(achievementId, settings)) {
            Ln.v(debugName(achievementId) + " already unlocked");
        } else {
            settings.unlockAchievement(achievementId);
        }
    }

    static void setRevealed(String achievementId, @NonNull GameSettings settings) {
        if (isRevealed(achievementId, settings)) {
            Ln.v(debugName(achievementId) + " already revealed");
        } else {
            settings.revealAchievement(achievementId);
        }
    }

    static boolean isUnlocked(String achievementId, @NonNull GameSettings settings) {
        return settings.isAchievementUnlocked(achievementId);
    }

    static boolean isRevealed(String achievementId, @NonNull GameSettings settings) {
        return settings.isAchievementRevealed(achievementId);
    }

}
