package com.ivygames.morskoiboi.model;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.games.snapshot.Snapshot;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;

import org.commons.logger.Ln;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProgressUtils {
    public static final String RANK = "rank";

    public static JSONObject toJson(Progress progress) {
        JSONObject json = new JSONObject();
        try {
            json.put(RANK, progress.getRank());
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

    public static Progress parseProgress(byte[] loadedData, Tracker tracker) {
        try {
            return fromJson(loadedData);
        } catch (JSONException je) {
            Ln.e(je);
            String corruptedData = new String(loadedData);
            ExceptionEvent.send(tracker, "parsing_progress", "data=" + corruptedData);

            // hacky solution to fix these corruptions: [rank=1575175], Progress [mRank=54397]
            if (corruptedData.length() > 3) {
                try {
                    int start = corruptedData.indexOf('=') + 1;
                    int end = corruptedData.lastIndexOf(']');
                    return new Progress(Integer.parseInt(corruptedData.substring(start, end)));
                } catch (Exception e) {
                    Ln.w(e);
                    ExceptionEvent.send(tracker, "parsing_progress", "data=" + corruptedData, e);
                }
            }

            return GameSettings.get().getProgress();
        }
    }

    public static Progress getProgressFromSnapshot(Snapshot snapshot, Tracker tracker) throws IOException {
        byte[] data = snapshot.getSnapshotContents().readFully();
        if (data == null || data.length == 0) {
            return new Progress(0);
        }

        return parseProgress(data, tracker);
    }
}
