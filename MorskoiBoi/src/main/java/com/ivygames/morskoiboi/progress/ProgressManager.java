package com.ivygames.morskoiboi.progress;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.analytics.Acra;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.acra.ACRA;
import org.commons.logger.Ln;

import java.io.IOException;

public class ProgressManager {

    public void debug_setProgress(int progress) {
        Ln.i("setting debug progress to: " + progress);
        saveProgress(new Progress(progress));
    }

    public static final String SNAPSHOT_NAME = "Snapshot-0";//"Sea Battle Snapshot";

    @NonNull
    private final GoogleApiClientWrapper mApiClient;

    @NonNull
    private final GameSettings mSettings;

    private SnapshotOpenResultListener mCallback = new SnapshotOpenResultListener() {

        @Override
        public void onConflict(String conflictId, Snapshot resolveSnapshot) {
            Ln.w("conflict while loading progress");
            resolveConflict(conflictId, resolveSnapshot);
        }

        @Override
        public void onUpdateServerWith(byte[] localProgress) {
            update(localProgress);
        }
    };

    public ProgressManager(@NonNull GoogleApiClientWrapper apiClient, @NonNull GameSettings settings) {
        mApiClient = apiClient;
        mSettings = settings;
    }

    public void loadProgress() {
        mApiClient.openAsynchronously(SNAPSHOT_NAME, new OpenSnapshotResultResultCallback(mSettings, mCallback));
    }

    public void incrementProgress(int increment) {
        if (increment <= 0) {
            ACRA.getErrorReporter().handleException(new RuntimeException("invalid increment: " + increment));
        }

        int oldScores = mSettings.getProgress().getScores();
        Ln.d("incrementing progress (" + oldScores + ") by " + increment);

        Progress newProgress = new Progress(oldScores + increment);
        saveProgress(newProgress);

        AnalyticsEvent.trackPromotionEvent(oldScores, newProgress.getScores());
    }

    private void saveProgress(@NonNull Progress newProgress) {
        mSettings.setProgress(newProgress);

        if (mApiClient.isConnected()) {
            Ln.d("posting progress to the cloud: " + newProgress);
            update(ProgressUtils.getBytes(newProgress));
        }
    }

    /**
     * Update the Snapshot in the Saved Games service with new data.  Metadata is not affected,
     * however for your own application you will likely want to update metadata such as cover image,
     * played time, and description with each Snapshot update.  After update, the UI will
     * be cleared.
     */
    private void update(final byte[] data) {

        AsyncTask<Void, Void, Boolean> updateTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                final boolean CREATE_IF_MISSING = true;
                Snapshots.OpenSnapshotResult open = mApiClient.open(SNAPSHOT_NAME, CREATE_IF_MISSING).await();
                if (open.getStatus().isSuccess()) {
                    return commitAndClose(open.getSnapshot());
                } else {
                    int statusCode = open.getStatus().getStatusCode();
                    Ln.w("Could not open Snapshot for update: " + statusCode);
                    if (statusCode == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                        try {
                            resolveConflict(open.getConflictId(), ProgressUtils.getResolveSnapshot(open));
                        } catch (IOException ioe) {
                            Ln.w(ioe, "could not resolve conflict during update");
                        }
                    }
                    return false;
                }
            }

            private boolean commitAndClose(Snapshot snapshot) {
                // Change data but leave existing metadata
                snapshot.getSnapshotContents().writeBytes(data);
                Snapshots.CommitSnapshotResult commit = mApiClient.commitAndClose(snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();
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
                    ACRA.getErrorReporter().handleException(new Acra("could not update"));
                }
            }
        };
        updateTask.execute();
    }

    private void resolveConflict(String conflictId, Snapshot snapshot) {
        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = mApiClient.resolveConflict(conflictId, snapshot);
        pendingResult.setResultCallback(new OpenSnapshotResultResultCallback(mSettings, mCallback));
    }

}


