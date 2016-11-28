package com.ivygames.morskoiboi.progress;

import android.support.annotation.NonNull;

import com.ivygames.common.analytics.ExceptionEvent;

import org.commons.logger.Ln;
import org.json.JSONException;
import org.json.JSONObject;

public class ProgressSerialization {
    private static final String RANK = "rank";

    @NonNull
    public static String toJson(int progress) {
        JSONObject json = new JSONObject();
        try {
            json.put(RANK, progress);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json.toString();
    }

    public static int fromJson(@NonNull String json) throws JSONException {
        return fromJson(new JSONObject(json));
    }

    private static int fromJson(@NonNull JSONObject json) throws JSONException {
        return json.getInt(RANK);
    }

    public static int parseProgress(@NonNull String json) {
        try {
            return fromJson(json);
        } catch (JSONException je) {
            Ln.e(je);
            ExceptionEvent.send("parsing_progress", "data=" + json);

            // hacky solution to fix these corruptions: [rank=1575175], Progress [mRank=54397]
            if (json.length() > 3) {
                try {
                    int start = json.indexOf('=') + 1;
                    int end = json.lastIndexOf(']');
                    return Integer.parseInt(json.substring(start, end));
                } catch (Exception e) {
                    Ln.w(e);
                    ExceptionEvent.send("parsing_progress2", "data=" + json, e);
                }
            }

            return 0;
        }
    }
}
