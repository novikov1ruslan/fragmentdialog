package com.ivygames.morskoiboi.achievement;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
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
        if (result.getStatus().isSuccess()) {
            Ln.d("saved game loaded");
            try {
                Snapshot snapshot = result.getSnapshot();
                byte[] data = snapshot.getSnapshotContents().readFully();
                Progress max = parseProgress(data);
                if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                    Snapshot modifiedSnapshot = result.getConflictingSnapshot();
                    data = modifiedSnapshot.getSnapshotContents().readFully();
                    Progress modifiedProgress = parseProgress(data);
                    max = getMax(max, modifiedProgress);

                    AnalyticsEvent.send(mGaTracker, "snapshot conflict");
                    AchievementsUtils.savedGamesUpdate(mApiClient, max.toJson().toString().getBytes());
                }

                Progress localProgress = mSettings.getProgress();
                Ln.v("local progress=" + localProgress + ", cloud progress=" + max + ", saving");
                max = getMax(max, localProgress);
                mSettings.setProgress(max);
            } catch (IOException ioe) {
                Ln.w(ioe, "failed to load saved game");
            }
        } else {
            Ln.w("failed to load saved game");
        }
    }

    private void resolveConflict(int stateKey, String resolvedVersion, byte[] localData, byte[] cloudData) {
        Progress localProgress = parseProgress(localData);
        Progress cloudProgress = parseProgress(cloudData);
        Progress max = getMax(localProgress, cloudProgress);
        Ln.d("resolving conflict: local=" + localProgress + " vs cloud=" + cloudProgress + ", resolved=" + max);
        byte[] resolvedData = max.toString().getBytes();
        AppStateManager.resolve(mApiClient, stateKey, resolvedVersion, resolvedData);
        mSettings.setProgress(max);
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
