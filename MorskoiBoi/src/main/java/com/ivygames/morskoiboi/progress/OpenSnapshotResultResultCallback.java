package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.commons.logger.Ln;

import java.io.IOException;

class OpenSnapshotResultResultCallback implements ResultCallback<Snapshots.OpenSnapshotResult> {

    @NonNull
    private final GameSettings mSettings;

    @NonNull
    private final SnapshotOpenResultListener mListener;

    OpenSnapshotResultResultCallback(@NonNull GameSettings settings, @NonNull SnapshotOpenResultListener callback) {
        mSettings = settings;
        mListener = callback;
    }

    @Override
    public void onResult(@NonNull Snapshots.OpenSnapshotResult result) {
        try {
            Status status = result.getStatus();
            if (status.isSuccess()) {
                processSuccessResult(result.getSnapshot());
            } else {
                if (status.getStatusCode() == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                    mListener.onConflict(result.getConflictId(), ProgressUtils.getResolveSnapshot(result));
                } else {
                    Ln.e("failed to load saved game: " + status.getStatusCode());
                }
            }
        } catch (IOException ioe) {
            Ln.w(ioe, "failed to load saved game");
        }
    }

    private void processSuccessResult(@NonNull Snapshot snapshot) throws IOException {
        Progress cloudProgress = ProgressUtils.getProgressFromSnapshot(snapshot);
        Progress localProgress = mSettings.getProgress();
        Ln.v("progress loaded: local =" + localProgress + ", cloud =" + cloudProgress);
        if (localProgress.getScores() > cloudProgress.getScores()) {
            AnalyticsEvent.send("save_game", "local_wins");
            mListener.onUpdateServerWith(ProgressUtils.getBytes(localProgress));
        } else if (cloudProgress.getScores() > localProgress.getScores()) {
            AnalyticsEvent.send("save_game", "cloud_wins");
            mSettings.setProgress(cloudProgress);
        }
    }

}
