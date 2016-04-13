package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;

import org.commons.logger.Ln;

import java.io.IOException;

final class SavedGamesResultCallback implements ResultCallback<Snapshots.OpenSnapshotResult> {

    @NonNull
    private final ProgressManager mProgressManager;

    SavedGamesResultCallback(@NonNull GoogleApiClientWrapper apiClient) {
        mProgressManager = new ProgressManager(apiClient, GameSettings.get());
    }

    @Override
    public void onResult(@NonNull Snapshots.OpenSnapshotResult result) {
        try {
            Status status = result.getStatus();
            if (status.isSuccess()) {
                mProgressManager.processSuccessResult(result.getSnapshot());
            } else {
                if (status.getStatusCode() == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                    Ln.w("conflict while loading progress");
                    resolveConflict(result);
                } else {
                    Ln.e("failed to load saved game: " + status.getStatusCode());
                }
            }
        } catch (IOException ioe) {
            Ln.w(ioe, "failed to load saved game");
        }
    }

    private void resolveConflict(Snapshots.OpenSnapshotResult result) throws IOException {
        mProgressManager.resolveConflict(result.getConflictId(), ProgressUtils.getResolveSnapshot(result));
    }

}
