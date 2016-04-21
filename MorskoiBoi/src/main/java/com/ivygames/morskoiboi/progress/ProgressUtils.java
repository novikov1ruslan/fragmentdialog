package com.ivygames.morskoiboi.progress;

import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.commons.logger.Ln;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProgressUtils {
    private static final String RANK = "rank";

    public static JSONObject toJson(Progress progress) {
        JSONObject json = new JSONObject();
        try {
            json.put(RANK, progress.getScores());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    public static Progress fromJson(byte[] json) throws JSONException {
        return fromJson(new JSONObject(new String(json)));
    }

    public static Progress fromJson(String json) throws JSONException {
        return fromJson(new JSONObject(json));
    }

    public static Progress fromJson(JSONObject json) throws JSONException {
        return new Progress(json.getInt(RANK));
    }

    public static byte[] getBytes(Progress progress) {
        return toJson(progress).toString().getBytes();
    }

    private static Progress parseProgress(byte[] loadedData) {
        try {
            return fromJson(loadedData);
        } catch (JSONException je) {
            Ln.e(je);
            String corruptedData = new String(loadedData);
            ExceptionEvent.send("parsing_progress", "data=" + corruptedData);

            // hacky solution to fix these corruptions: [rank=1575175], Progress [mRank=54397]
            if (corruptedData.length() > 3) {
                try {
                    int start = corruptedData.indexOf('=') + 1;
                    int end = corruptedData.lastIndexOf(']');
                    return new Progress(Integer.parseInt(corruptedData.substring(start, end)));
                } catch (Exception e) {
                    Ln.w(e);
                    ExceptionEvent.send("parsing_progress", "data=" + corruptedData, e);
                }
            }

            return new Progress(0);
        }
    }

    public static Progress getProgressFromSnapshot(Snapshot snapshot) throws IOException {
        byte[] data = snapshot.getSnapshotContents().readFully();
        if (data == null || data.length == 0) {
            return new Progress(0);
        }

        return parseProgress(data);
    }

    public static Snapshot getResolveSnapshot(Snapshots.OpenSnapshotResult result) throws IOException {
        Snapshot baseSnapshot = result.getSnapshot();
        Snapshot conflictingSnapshot = result.getConflictingSnapshot();
        int baseScores = getScoresFromSnapshot(baseSnapshot);
        int conflictingScores = getScoresFromSnapshot(conflictingSnapshot);

        return baseScores > conflictingScores ? baseSnapshot : conflictingSnapshot;
    }

    private static int getScoresFromSnapshot(Snapshot snapshot) throws IOException {
        return getProgressFromSnapshot(snapshot).getScores();
    }
}
