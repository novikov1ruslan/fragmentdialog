package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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
            if (result.getStatus().isSuccess()) {
                Progress max = getServerProgress(result.getSnapshot());
                Ln.d("saved game loaded");
                Progress localProgress = mSettings.getProgress();
                Ln.v("local progress=" + localProgress + ", cloud progress=" + max + ", saving");
                max = getMax(max, localProgress);
                mSettings.setProgress(max);
            } else {
                Ln.w("failed to load saved game");
                int statusCode = result.getStatus().getStatusCode();
                if (statusCode == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                    Progress max = getServerProgress(result.getSnapshot());
                    max = resolveConflict(result, max);
                    mSettings.setProgress(max);
                } else {
                    Ln.e("failed to load saved game: " + statusCode);
                }
            }

        } catch (IOException ioe) {
            Ln.w(ioe, "failed to load saved game");
        }
    }

    private Progress getServerProgress(Snapshot snapshot) throws IOException {
        byte[] data = snapshot.getSnapshotContents().readFully();
        if (data == null || data.length < 6) {
            return new Progress(0);
        }

        return parseProgress(data);
    }

    @NonNull
    private Progress resolveConflict(Snapshots.OpenSnapshotResult result, Progress max) throws IOException {
        Snapshot modifiedSnapshot = result.getConflictingSnapshot();
        byte[] data = modifiedSnapshot.getSnapshotContents().readFully();
        Progress modifiedProgress = parseProgress(data);
        max = getMax(max, modifiedProgress);

        AnalyticsEvent.send(mGaTracker, "snapshot conflict");
        ProgressManager.savedGamesUpdate(mApiClient, max.toJson().toString().getBytes());
        return max;
    }

    private Progress parseProgress(byte[] loadedData) {
        Progress progress;
        try {
            progress = Progress.fromJson(loadedData);
        } catch (JSONException je) {
            progress = mSettings.getProgress();
            Ln.e(je);
            mGaTracker.send(new ExceptionEvent("parsing_progress", "data=" + new String(loadedData), 1).build());
        }
        return progress;
    }

    private Progress getMax(Progress local, Progress cloud) {
        return local.getRank() > cloud.getRank() ? local : cloud;
    }

}
