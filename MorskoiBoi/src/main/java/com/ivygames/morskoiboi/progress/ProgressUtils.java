package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.model.Progress;

import org.commons.logger.Ln;
import org.json.JSONException;
import org.json.JSONObject;

public class ProgressUtils {
    private static final String RANK = "rank";

    @NonNull
    public static JSONObject toJson(@NonNull Progress progress) {
        JSONObject json = new JSONObject();
        try {
            json.put(RANK, progress.getScores());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    @NonNull
    public static Progress fromJson(@NonNull byte[] json) throws JSONException {
        return fromJson(new JSONObject(new String(json)));
    }

    @NonNull
    public static Progress fromJson(@NonNull String json) throws JSONException {
        return fromJson(new JSONObject(json));
    }

    @NonNull
    public static Progress fromJson(@NonNull JSONObject json) throws JSONException {
        return new Progress(json.getInt(RANK));
    }

    @NonNull
    public static byte[] getBytes(@NonNull Progress progress) {
        return toJson(progress).toString().getBytes();
    }

    public static Progress parseProgress(@NonNull byte[] loadedData) {
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

}
