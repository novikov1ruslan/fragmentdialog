package com.ivygames.morskoiboi.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Progress {

    public Progress(int rank) {
        mRank = rank;
    }

    private static final String RANK = "rank";

    private final int mRank;

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(RANK, mRank);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    public static Progress fromJson(byte[] json) throws JSONException {
        return Progress.fromJson(new JSONObject(new String(json)));
    }

    public static Progress fromJson(String json) throws JSONException {
        return Progress.fromJson(new JSONObject(json));
    }

    public static Progress fromJson(JSONObject json) throws JSONException {
        return new Progress(json.getInt(RANK));
    }

    public int getRank() {
        return mRank;
    }

    @Override
    public String toString() {
        return "[rank=" + mRank + "]";
    }

    public static byte[] getBytes(Progress progress) {
        return progress.toJson().toString().getBytes();
    }
}
