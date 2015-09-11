package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.model.ProgressUtils;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.io.IOException;

final class SavedGamesResultCallback implements ResultCallback<Snapshots.OpenSnapshotResult> {
    private final GameSettings mSettings = GameSettings.get();
    private final GoogleApiClient mApiClient;
    private final Tracker mGaTracker;

    SavedGamesResultCallback(GoogleApiClient apiClient, Tracker tracker) {
        mApiClient = Validate.notNull(apiClient);
        mGaTracker = Validate.notNull(tracker);
    }

    @Override
    public void onResult(Snapshots.OpenSnapshotResult result) {
        try {
            Status status = result.getStatus();
            if (status.isSuccess()) {
                Progress cloudProgress = ProgressUtils.getProgressFromSnapshot(result.getSnapshot());
                Progress localProgress = mSettings.getProgress();
                Ln.v("progress loaded: local =" + localProgress + ", cloud =" + cloudProgress);
                Progress max = getMax(cloudProgress, localProgress);
                mSettings.setProgress(max);
                if (max.getRank() > cloudProgress.getRank()) {
                    ProgressManager.update(mApiClient, ProgressUtils.getBytes(max));// max.toJson().toString().getBytes());
                }
            } else {
                int statusCode = status.getStatusCode();
                if (statusCode == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                    Ln.w("conflict while loading progress");
                    AnalyticsEvent.send(mGaTracker, "snapshot conflict");
                    resolveConflict(result);
                } else {
                    Ln.e("failed to load saved game: " + statusCode);
                }
            }
        } catch (IOException ioe) {
            Ln.w(ioe, "failed to load saved game");
        }
    }

    @NonNull
    private void resolveConflict(Snapshots.OpenSnapshotResult result) throws IOException {
        Progress currentProgress = ProgressUtils.getProgressFromSnapshot(result.getSnapshot());
        Progress modifiedProgress = ProgressUtils.getProgressFromSnapshot(result.getConflictingSnapshot());
        Progress max = getMax(currentProgress, modifiedProgress);

        mSettings.setProgress(max);
        ProgressManager.update(mApiClient, ProgressUtils.getBytes(max));
    }

    private static Progress getMax(Progress local, Progress cloud) {
        return local.getRank() > cloud.getRank() ? local : cloud;
    }

}
