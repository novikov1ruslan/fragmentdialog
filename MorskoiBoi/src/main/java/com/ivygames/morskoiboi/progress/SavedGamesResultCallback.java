package com.ivygames.morskoiboi.progress;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshots;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.io.IOException;

final class SavedGamesResultCallback implements ResultCallback<Snapshots.OpenSnapshotResult> {
    private final GoogleApiClient mApiClient;

    SavedGamesResultCallback(GoogleApiClient apiClient) {
        mApiClient = Validate.notNull(apiClient);
    }

    @Override
    public void onResult(Snapshots.OpenSnapshotResult result) {
        try {
            Status status = result.getStatus();
            if (status.isSuccess()) {
                ProgressManager.processSuccessResult(mApiClient, result.getSnapshot());
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
        ProgressManager.resolveConflict(mApiClient, result.getConflictId(), ProgressUtils.getResolveSnapshot(result));
    }

}
