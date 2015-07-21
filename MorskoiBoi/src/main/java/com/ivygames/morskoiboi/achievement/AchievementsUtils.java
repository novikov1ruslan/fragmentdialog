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
    static final int STATE_KEY = 0;

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

    public static void incrementProgress(int increment, GoogleApiClient apiClient, Tracker tracker) {
        Ln.d("incrementing progress by " + increment);
        Progress progress = GameSettings.get().getProgress();
        int oldProgress = progress.getRank();
        GameSettings.get().setProgress(new Progress(oldProgress + increment));

        trackPromotionEvent(oldProgress, progress.getRank(), tracker);

        Ln.d("posting progress to the cloud: " + progress);
        String json = progress.toJson().toString();
        if (apiClient.isConnected()) {
            savedGamesUpdate(apiClient, json.getBytes());
//            AppStateManager.update(apiClient, STATE_KEY, json.getBytes());
        }
    }

    private static void trackPromotionEvent(int oldProgress, int newProgress, Tracker tracker) {
        Rank lastRank = Rank.getBestRankForScore(oldProgress);
        Rank newRank = Rank.getBestRankForScore(newProgress);
        if (newRank != lastRank) {
            GameSettings.get().newRankAchieved(true);
            String label = lastRank + " promoted to " + newRank;
            tracker.send(new AnalyticsEvent("promotion", label, 1).build());
        }
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

    /**
     * Update the Snapshot in the Saved Games service with new data.  Metadata is not affected,
     * however for your own application you will likely want to update metadata such as cover image,
     * played time, and description with each Snapshot update.  After update, the UI will
     * be cleared.
     */
    public static void savedGamesUpdate(final GoogleApiClient apiClient, final byte[] data) {
        final String snapshotName = makeSnapshotName();
        final boolean createIfMissing = false;

        AsyncTask<Void, Void, Boolean> updateTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult open = Games.Snapshots.open(apiClient, snapshotName, createIfMissing).await();

                if (!open.getStatus().isSuccess()) {
                    Ln.w("Could not open Snapshot for update.");
                    return false;
                }

                // Change data but leave existing metadata
                Snapshot snapshot = open.getSnapshot();
                snapshot.getSnapshotContents().writeBytes(data);

                Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(
                        apiClient, snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();

                if (!commit.getStatus().isSuccess()) {
                    Ln.w("Failed to commit Snapshot.");
                    return false;
                }

                // No failures
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Ln.d("saved data updated");
                } else {
                    ACRA.getErrorReporter().handleException(new RuntimeException("could not update"));
                }
            }
        };
        updateTask.execute();
    }

    /**
     * Generate a unique Snapshot name from an AppState stateKey.
     *
     * @param appStateKey the stateKey for the Cloud Save data.
     * @return a unique Snapshot name that maps to the stateKey.
     */
    private static String makeSnapshotName(int appStateKey) {
        return "Snapshot-" + String.valueOf(appStateKey);
    }

    public static String makeSnapshotName() {
        return makeSnapshotName(STATE_KEY);
    }
}
