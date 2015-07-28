package com.ivygames.morskoiboi.progress;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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

public class ProgressManager {
    public static final int STATE_KEY = 0;

    private final GoogleApiClient mApiClient;
    private final Tracker mGaTracker;
    //    private final AppStateResultCallback appStateCallback;

    public ProgressManager(GoogleApiClient apiClient, Tracker tracker) {
        mApiClient = apiClient;
        mGaTracker = tracker;
        //        appStateCallback = new AppStateResultCallback(apiClient, tracker);
    }

    public void loadProgress(Bitmap bitmap) {
        //            PendingResult<StateResult> stateResult = AppStateManager.load(mApiClient, AchievementsUtils.STATE_KEY);
//            stateResult.setResultCallback(appStateCallback);
        if (GameSettings.get().hasProgressMigrated()) {
            savedGamesLoad(makeSnapshotName());
        } else {
            cloudSaveMigrate(bitmap);
        }
    }

    /**
     * Async migrate the data in Cloud Save (stateKey APP_STATE_KEY) to a Snapshot in the Saved
     * Games service with unique snap 'Snapshot-{APP_STATE_KEY}'.  If no such Snapshot exists,
     * create a Snapshot and populate all fields.  If the Snapshot already exists, update the
     * appropriate data and metadata.  After migrate, the UI will be cleared and the data will be
     * available to load from Snapshots.
     */
    public void cloudSaveMigrate(final Bitmap bitmap) {
        final boolean createIfMissing = true;

        // Note: when migrating your users from Cloud Save to Saved Games, you will need to perform
        // the migration process at most once per device.  You should keep track of the migration
        // status locally for each AppState data slot (using SharedPreferences or similar)
        // to avoid repeating network calls or migrating the same AppState data multiple times.

        // Compute SnapshotMetadata fields based on the information available from AppState.  In
        // this case there is no data available to auto-generate a description, cover image, or
        // playedTime.  It is strongly recommended that you generate unique and meaningful
        // values for these fields based on the data in your app.
        final String snapshotName = makeSnapshotName();
        final String description = "Sea Battle Score";
        final long playedTimeMillis = 0;

        AsyncTask<Void, Void, Boolean> migrateTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                // Get AppState Data
                AppStateManager.StateResult load = AppStateManager.load(mApiClient, STATE_KEY).await();

                if (!load.getStatus().isSuccess()) {
                    Ln.w("Could not load App State for migration.");
                    return true;
                }

                // Get Data from AppState
                byte[] data = load.getLoadedResult().getLocalData();

                // Open the snapshot, creating if necessary
                Snapshots.OpenSnapshotResult open = Games.Snapshots.open(mApiClient, snapshotName, createIfMissing).await();

                if (!open.getStatus().isSuccess()) {
                    Ln.w("Could not open Snapshot for migration.");
                    // TODO: Handle Snapshot conflicts
                    // Note: one reason for failure to open a Snapshot is conflicting saved games.
                    // This is outside the scope of this sample, however you should resolve such
                    // conflicts in your own app by following the steps outlined here:
                    // https://developers.google.com/games/services/android/savedgames#handling_saved_game_conflicts
                    return true;
                }

                // Write the new data to the snapshot
                Snapshot snapshot = open.getSnapshot();
                snapshot.getSnapshotContents().writeBytes(data);

                // Change metadata
                SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                        .fromMetadata(snapshot.getMetadata())
                        .setCoverImage(bitmap)
                        .setDescription(description)
                        .setPlayedTimeMillis(playedTimeMillis)
                        .build();

                Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(mApiClient, snapshot, metadataChange).await();

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
                    GameSettings.get().setProgressMigrated();
                    AnalyticsEvent.send(mGaTracker, "migration succeeded");
                    savedGamesLoad(makeSnapshotName());
                } else {
                    ACRA.getErrorReporter().handleException(new RuntimeException("migration failed"));
                }
            }
        };
        migrateTask.execute();
    }

    /**
     * Load a Snapshot from the Saved Games service based on its unique name.  After load, the UI
     * will update to display the Snapshot data and SnapshotMetadata.
     *
     * @param snapshotName the unique name of the Snapshot.
     */
    private void savedGamesLoad(String snapshotName) {
        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.open(
                mApiClient, snapshotName, false);

        ResultCallback<Snapshots.OpenSnapshotResult> callback = new SavedGamesResultCallback(mApiClient, mGaTracker);
        pendingResult.setResultCallback(callback);
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

    /**
     * Update the Snapshot in the Saved Games service with new data.  Metadata is not affected,
     * however for your own application you will likely want to update metadata such as cover image,
     * played time, and description with each Snapshot update.  After update, the UI will
     * be cleared.
     */
    static void savedGamesUpdate(final GoogleApiClient apiClient, final byte[] data) {
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


