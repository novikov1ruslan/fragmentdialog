package com.ivygames.morskoiboi.achievement;

import android.os.AsyncTask;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Rank;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.acra.ACRA;
import org.commons.logger.Ln;

public class AchievementsUtils {
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

    static void setUnlocked(String achievementId) {
        if (AchievementsUtils.isUnlocked(achievementId)) {
            Ln.v(AchievementsUtils.debugName(achievementId) + " already unlocked");
        } else {
            GameSettings.get().unlockAchievement(achievementId);
        }
    }

    static void setRevealed(String achievementId) {
        if (AchievementsUtils.isRevealed(achievementId)) {
            Ln.v(AchievementsUtils.debugName(achievementId) + " already revealed");
        } else {
            GameSettings.get().revealAchievement(achievementId);
        }
    }

    static boolean isUnlocked(String achievementId) {
        return GameSettings.get().isAchievementUnlocked(achievementId);
    }

    static boolean isRevealed(String achievementId) {
        return GameSettings.get().isAchievementRevealed(achievementId);
    }

}
