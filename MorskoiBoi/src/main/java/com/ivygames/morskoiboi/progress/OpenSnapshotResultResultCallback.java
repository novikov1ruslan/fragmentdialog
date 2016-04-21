package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.commons.logger.Ln;

import java.io.IOException;

class OpenSnapshotResultResultCallback implements ResultCallback<Snapshots.OpenSnapshotResult> {

    @NonNull
    private final Progress mLocalProgress;

    @NonNull
    private final SnapshotOpenResultListener mListener;

    OpenSnapshotResultResultCallback(@NonNull Progress localProgress,
                                     @NonNull SnapshotOpenResultListener callback) {
        mLocalProgress = localProgress;
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
        } catch (IOException e) {
            Ln.w(e, "failed to load saved game");
        }
    }

    private void processSuccessResult(@NonNull Snapshot snapshot) throws IOException {
        Progress cloudProgress = ProgressUtils.getProgressFromSnapshot(snapshot);
        Ln.v("progress loaded: local =" + mLocalProgress + ", cloud =" + cloudProgress);
        if (mLocalProgress.getScores() > cloudProgress.getScores()) {
            AnalyticsEvent.send("save_game", "local_wins");
            mListener.onUpdateServerWith(ProgressUtils.getBytes(mLocalProgress));
        } else if (cloudProgress.getScores() > mLocalProgress.getScores()) {
            AnalyticsEvent.send("save_game", "cloud_wins");
            mListener.onUpdateLocalWith(cloudProgress);
        }
    }

}
