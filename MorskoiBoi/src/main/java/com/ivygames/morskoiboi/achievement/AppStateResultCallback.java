package com.ivygames.morskoiboi.achievement;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.appstate.AppStateManager.StateConflictResult;
import com.google.android.gms.appstate.AppStateManager.StateLoadedResult;
import com.google.android.gms.appstate.AppStateManager.StateResult;
import com.google.android.gms.appstate.AppStateStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.GamesStatusCodes;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;
import org.json.JSONException;

final class AppStateResultCallback implements ResultCallback<AppStateManager.StateResult> {
    private final GameSettings mSettings = GameSettings.get();
    private final GoogleApiClient mApiClient;
    private final Tracker mGaTracker;

    AppStateResultCallback(GoogleApiClient apiClient, Tracker tracker) {
        Validate.notNull(apiClient);
        mApiClient = apiClient;

        Validate.notNull(tracker);
        mGaTracker = tracker;
    }

    @Override
    public void onResult(StateResult result) {
        // either of these 2 will be null
        StateConflictResult conflictResult = result.getConflictResult();
        StateLoadedResult loadedResult = result.getLoadedResult();
        if (conflictResult != null) {
            Ln.v("conflict state loaded successfully: " + conflictResult.getStatus().isSuccess());
            byte[] localData = conflictResult.getLocalData();
            byte[] serverData = conflictResult.getServerData();
            String resolvedVersion = conflictResult.getResolvedVersion();
            resolveConflict(AchievementsUtils.STATE_KEY, resolvedVersion, localData, serverData);
        } else if (loadedResult != null) {
            int statusCode = loadedResult.getStatus().getStatusCode();
            int stateKey = loadedResult.getStateKey();
            byte[] localData = loadedResult.getLocalData();
            processLoadedData(statusCode, stateKey, localData);
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

    private void processLoadedData(int statusCode, int stateKey, byte[] loadedData) {
        Ln.v("status code=" + statusCode + "; state key=" + stateKey);
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                Ln.d("Data was successfully loaded from the cloud: merge with local data.");
                Progress localProgress = mSettings.getProgress();
                Progress cloudProgress = parseProgress(loadedData);
                Ln.v("local progress=" + localProgress + ", cloud progress=" + cloudProgress);
                mSettings.setProgress(getMax(localProgress, cloudProgress));
                break;
            case AppStateStatusCodes.STATUS_STATE_KEY_NOT_FOUND:
            /*
			 * key not found means there is no saved data. To us, this is the same as having empty data, so we treat this as a success.
			 */
                Progress progress = mSettings.getProgress();
                Ln.d("there is no saved data, updating cloud with: " + progress);
                String json = progress.toJson().toString();
                AppStateManager.update(mApiClient, AchievementsUtils.STATE_KEY, json.getBytes());
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
                // TODO:
                // can't reach cloud, and we have no local state. Warn user that
                // they may not see their existing progress, but any new progress
                // won't be lost.
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA:
                Ln.w("can't reach cloud, but we have locally cached data.");
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                Ln.w("need to reconnect AppStateClient");
                mApiClient.connect();
                break;
            default:
                Ln.w("unprocessed status: " + statusCode);
                break;
        }
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
