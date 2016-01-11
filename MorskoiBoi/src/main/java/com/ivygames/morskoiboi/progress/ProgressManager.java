package com.ivygames.morskoiboi.progress;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.acra.ACRA;
import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.io.IOException;

public class ProgressManager {
    public static final String SNAPSHOT_NAME = "Snapshot-0";//"Sea Battle Snapshot";

    private final GoogleApiClient mApiClient;

    public ProgressManager(GoogleApiClient apiClient) {
        mApiClient = Validate.notNull(apiClient);
    }

    public static void processSuccessResult(GoogleApiClient apiClient, Snapshot snapshot) throws IOException {
        Progress cloudProgress = ProgressUtils.getProgressFromSnapshot(snapshot);
        Progress localProgress = GameSettings.get().getProgress();
        Ln.v("progress loaded: local =" + localProgress + ", cloud =" + cloudProgress);
        if (localProgress.getScores() > cloudProgress.getScores()) {
            AnalyticsEvent.send("save_game", "local_wins");
            update(apiClient, ProgressUtils.getBytes(localProgress));
        } else if (cloudProgress.getScores() > localProgress.getScores()) {
            AnalyticsEvent.send("save_game", "cloud_wins");
            GameSettings.get().setProgress(cloudProgress);
        }
    }

    public void loadProgress() {
        Games.Snapshots.open(mApiClient, SNAPSHOT_NAME, false).setResultCallback(new SavedGamesResultCallback(mApiClient));
    }

    public void incrementProgress(int increment) {
        if (increment <= 0) {
            ACRA.getErrorReporter().handleException(new RuntimeException("invalid increment: " + increment));
        }

        int oldScores = GameSettings.get().getProgress().getScores();
        Ln.d("incrementing progress (" + oldScores + ") by " + increment);

        Progress newProgress = new Progress(oldScores + increment);
        saveProgress(newProgress);

        AnalyticsEvent.trackPromotionEvent(oldScores, newProgress.getScores());
    }

    private void saveProgress(Progress newProgress) {
        GameSettings.get().setProgress(newProgress);

        if (mApiClient.isConnected()) {
            Ln.d("posting progress to the cloud: " + newProgress);
            update(mApiClient, ProgressUtils.getBytes(newProgress));
        }
    }

    public void debug_setProgress(int progress) {
        Ln.i("setting debug progress to: " + progress);

        saveProgress(new Progress(progress));
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
                    int statusCode = open.getStatus().getStatusCode();
                    Ln.w("Could not open Snapshot for update: " + statusCode);
                    if (statusCode == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                        try {
                            resolveConflict(apiClient, open.getConflictId(), ProgressUtils.getResolveSnapshot(open));
                        } catch (IOException ioe) {
                            Ln.w(ioe, "could not resolve conflict during update");
                        }
                    }
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

    public static void resolveConflict(final GoogleApiClient apiClient, String conflictId, Snapshot snapshot) {
        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.resolveConflict(apiClient, conflictId, snapshot);
        pendingResult.setResultCallback(new ResultCallback<Snapshots.OpenSnapshotResult>() {
                                            @Override
                                            public void onResult(Snapshots.OpenSnapshotResult result) {
                                                Status status = result.getStatus();
                                                if (status.isSuccess()) {
                                                    Ln.d("conflict solved successfully");
                                                    AnalyticsEvent.send("conflict_solved");
                                                    try {
                                                        processSuccessResult(apiClient, result.getSnapshot());
                                                    } catch (IOException ioe) {
                                                        Ln.w(ioe, "failed to process conflict result");
                                                    }
                                                } else {
                                                    int statusCode = status.getStatusCode();
                                                    Ln.w("conflict_failed_" + statusCode);
                                                }
                                            }
                                        }

        );
    }
}


