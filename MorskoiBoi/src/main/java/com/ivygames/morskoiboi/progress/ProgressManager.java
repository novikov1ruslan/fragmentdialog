package com.ivygames.morskoiboi.progress;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.model.Progress;

import org.commons.logger.Ln;

import java.io.IOException;

import static com.google.android.gms.games.snapshot.Snapshots.CommitSnapshotResult;
import static com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult;
import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class ProgressManager {

    private static final boolean USE_GAME_SAVE_SERVICE = true;

    public void debug_setProgress(int progress) {
        Ln.i("setting debug progress to: " + progress);
        Progress newProgress = new Progress(progress);
        mSettings.setProgress(newProgress);
        updateProgress(newProgress);
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
        // TODO: setting do not belong to this class
        mSettings = settings;
    }

    public void loadProgress() {
        mApiClient.openAsynchronously(SNAPSHOT_NAME, new OpenSnapshotResultResultCallback(mSettings, mCallback));
    }

    public void updateProgress(Progress newProgress) {
        if (USE_GAME_SAVE_SERVICE) {
            if (mApiClient.isConnected()) {
                Ln.d("posting progress to the cloud: " + newProgress);
                update(ProgressUtils.getBytes(newProgress));
            }
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
                OpenSnapshotResult open = mApiClient.open(SNAPSHOT_NAME, CREATE_IF_MISSING).await();
                if (open.getStatus().isSuccess()) {
                    return commitAndClose(open.getSnapshot());
                } else {
                    int statusCode = open.getStatus().getStatusCode();
                    Ln.w("Could not open Snapshot for update: " + GamesStatusCodes.getStatusString(statusCode));
                    if (statusCode == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                        try {
                            resolveConflict(open.getConflictId(), ProgressUtils.getResolveSnapshot(open));
                        } catch (IOException e) {
                            Ln.w(e, "could not resolve conflict during update");
                        }
                    }
                    return false;
                }
            }

            private boolean commitAndClose(Snapshot snapshot) {
                // Change data but leave existing metadata
                snapshot.getSnapshotContents().writeBytes(data);
                CommitSnapshotResult commit = mApiClient.commitAndClose(snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();
                com.google.android.gms.common.api.Status status = commit.getStatus();
                if (status.isSuccess()) {
                    return true;
                } else {
                    String commonCode = CommonStatusCodes.getStatusCodeString(status.getStatusCode());
//                    String gameCode = GamesStatusCodes.getStatusString(status.getStatusCode());
                    Ln.w("Failed to commit Snapshot: " + commonCode);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Ln.d("saved data updated");
                } else {
                    reportException("could not update");
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


