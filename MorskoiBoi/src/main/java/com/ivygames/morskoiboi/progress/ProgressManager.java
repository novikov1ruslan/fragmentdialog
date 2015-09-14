package com.ivygames.morskoiboi.progress;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.model.ProgressUtils;

import org.acra.ACRA;
import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class ProgressManager {
    public static final String SNAPSHOT_NAME = "Snapshot-0";//"Sea Battle Snapshot";

    private final GoogleApiClient mApiClient;

    public ProgressManager(GoogleApiClient apiClient) {
        mApiClient = Validate.notNull(apiClient);
    }

    public void loadProgress() {
        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.open(mApiClient, SNAPSHOT_NAME, false);
        pendingResult.setResultCallback(new SavedGamesResultCallback(mApiClient));
    }

    public void incrementProgress(int increment) {
        int oldProgress = GameSettings.get().getProgress().getRank();
        Ln.d("incrementing progress (" + oldProgress + ") by " + increment);

        Progress newProgress = new Progress(oldProgress + increment);
        GameSettings.get().setProgress(newProgress);

        if (mApiClient.isConnected()) {
            Ln.d("posting progress to the cloud: " + newProgress);
            update(mApiClient, ProgressUtils.getBytes(newProgress));
        }

        AnalyticsEvent.trackPromotionEvent(oldProgress, newProgress.getRank());
    }

    /**
     * Update the Snapshot in the Saved Games service with new data.  Metadata is not affected,
     * however for your own application you will likely want to update metadata such as cover image,
     * played time, and description with each Snapshot update.  After update, the UI will
     * be cleared.
     */
    static void update(final GoogleApiClient apiClient, final byte[] data) {
        final boolean CREATE_IF_MISSING = true;

        AsyncTask<Void, Void, Boolean> updateTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult open = Games.Snapshots.open(apiClient, SNAPSHOT_NAME, CREATE_IF_MISSING).await();
                if (!open.getStatus().isSuccess()) {
                    Ln.w("Could not open Snapshot for update: " + open.getStatus().getStatusCode());
                    return false;
                }
                Snapshot snapshot = open.getSnapshot();

                // Change data but leave existing metadata
                snapshot.getSnapshotContents().writeBytes(data);
                Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(apiClient, snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();
                if (!commit.getStatus().isSuccess()) {
                    Ln.w("Failed to commit Snapshot: " + commit.getStatus().getStatusCode());
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

}


