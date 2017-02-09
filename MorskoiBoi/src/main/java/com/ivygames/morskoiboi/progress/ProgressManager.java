package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.morskoiboi.GameSettings;

import org.commons.logger.Ln;
import org.commons.logger.LoggerUtils;

import java.io.IOException;

public class ProgressManager {
    private static final String SNAPSHOT_NAME = "Snapshot-0";//"Sea Battle Snapshot";

    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final GameSettings mSettings;

    public ProgressManager(@NonNull ApiClient apiClient, @NonNull GameSettings settings) {
        mApiClient = apiClient;
        mSettings = settings;
    }

    public void synchronize() {
        if (mApiClient.isConnected()) {
            Ln.v("synchronizing progress...");
            mApiClient.openAsynchronously(SNAPSHOT_NAME, new OpenSnapshotResultResultCallback());
        } else {
            Ln.e("not connected, cannot synchronize progress");
        }
    }

    public boolean isConnected() {
        return mApiClient.isConnected();
    }

    private class OpenSnapshotResultResultCallback implements ResultCallback<Snapshots.OpenSnapshotResult> {
        private static final int MAX_REPETITIONS = 10;

        private int mRepetitions;

        @Override
        public void onResult(@NonNull Snapshots.OpenSnapshotResult result) {
            try {
                Status status = result.getStatus();
                if (status.isSuccess()) {
                    Ln.v("result received, processing...");
                    processSuccessResult(result.getSnapshot());
                } else {
                    if (status.getStatusCode() == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                        Ln.v("resolving conflict...");
                        resolveConflict(result.getConflictId(), getResolveSnapshot(result));
                    } else {
                        Ln.w("failed to load saved game: " + GamesStatusCodes.getStatusString(status.getStatusCode()));
                    }
                }
            } catch (Exception e) {
                Ln.w("error parsing progress");
                ExceptionHandler.reportException(e);
            }
        }

        private void resolveConflict(@NonNull String conflictId, @NonNull Snapshot snapshot) {
            mRepetitions++;
            if (mRepetitions > MAX_REPETITIONS) {
                Ln.w("max repetitions reached");
                return;
            }

            PendingResult<Snapshots.OpenSnapshotResult> result = mApiClient.resolveConflict(conflictId, snapshot);
            if (result != null) {
                result.setResultCallback(this);
            }
        }

        private void processSuccessResult(@NonNull Snapshot snapshot) throws Exception {
            int cloudProgress = getProgressFromSnapshot(snapshot);
            int localProgress = mSettings.getProgress();
            Ln.v("local =" + localProgress + ", cloud =" + cloudProgress);
            if (localProgress > cloudProgress) {
                AnalyticsEvent.send("save_game", "local_wins");
                Ln.d("updating remote with: " + localProgress);
                commitAndClose(snapshot, getBytes(localProgress));
            } else if (cloudProgress > localProgress) {
                AnalyticsEvent.send("save_game", "cloud_wins");
                Ln.d("updating local with: " + cloudProgress);
                mSettings.setProgress(cloudProgress);
            }
        }

        private void commitAndClose(@NonNull Snapshot snapshot, @NonNull byte[] data) throws Exception {
            // Change data but leave existing metadata
            snapshot.getSnapshotContents().writeBytes(data);
            mApiClient.commitAndClose(snapshot, SnapshotMetadataChange.EMPTY_CHANGE);
        }

    }

    private static Snapshot getResolveSnapshot(@NonNull Snapshots.OpenSnapshotResult result) throws IOException {
        Snapshot baseSnapshot = result.getSnapshot();
        Snapshot conflictingSnapshot = result.getConflictingSnapshot();
        int baseScores = getScoresFromSnapshot(baseSnapshot);
        int conflictingScores = getScoresFromSnapshot(conflictingSnapshot);

        Ln.v("base scores: " + baseScores + ", conflicting scores: " + conflictingScores);
        return baseScores > conflictingScores ? baseSnapshot : conflictingSnapshot;
    }

    private static int getScoresFromSnapshot(Snapshot snapshot) throws IOException {
        return getProgressFromSnapshot(snapshot);
    }

    private static int getProgressFromSnapshot(@NonNull Snapshot snapshot) throws IOException {
        byte[] data = snapshot.getSnapshotContents().readFully();
        if (data == null || data.length == 0) {
            return 0;
        }

        return parseProgress(data);
    }

    @NonNull
    private static byte[] getBytes(int progress) {
        return ProgressSerialization.toJson(progress).getBytes();
    }

    private static int parseProgress(@NonNull byte[] loadedData) {
        return ProgressSerialization.parseProgress(new String(loadedData));
    }

    @Override
    public String toString() {
        return LoggerUtils.getSimpleName(this);
    }
}
