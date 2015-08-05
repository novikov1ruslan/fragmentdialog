package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;
import org.json.JSONException;

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
                Progress cloudProgress = getProgressFromSnapshot(result.getSnapshot());
                Progress localProgress = mSettings.getProgress();
                Ln.v("progress loaded: local =" + localProgress + ", cloud =" + cloudProgress);
                Progress max = getMax(cloudProgress, localProgress);
                mSettings.setProgress(max);
                if (max.getRank() > cloudProgress.getRank()) {
                    ProgressManager.update(mApiClient, Progress.getBytes(max));// max.toJson().toString().getBytes());
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

    private Progress getProgressFromSnapshot(Snapshot snapshot) throws IOException {
        byte[] data = snapshot.getSnapshotContents().readFully();
        if (data == null || data.length == 0) {
            return new Progress(0);
        }

        return parseProgress(data);
    }

    @NonNull
    private void resolveConflict(Snapshots.OpenSnapshotResult result) throws IOException {
        Progress currentProgress = getProgressFromSnapshot(result.getSnapshot());
        Progress modifiedProgress = getProgressFromSnapshot(result.getConflictingSnapshot());
        Progress max = getMax(currentProgress, modifiedProgress);

        mSettings.setProgress(max);
        ProgressManager.update(mApiClient, Progress.getBytes(max));
    }

    private Progress parseProgress(byte[] loadedData) {
        try {
            return Progress.fromJson(loadedData);
        } catch (JSONException je) {
            Ln.e(je);
            mGaTracker.send(new ExceptionEvent("parsing_progress", "data=" + new String(loadedData), 1).build());
            return mSettings.getProgress();
        }
    }

    private static Progress getMax(Progress local, Progress cloud) {
        return local.getRank() > cloud.getRank() ? local : cloud;
    }

}
