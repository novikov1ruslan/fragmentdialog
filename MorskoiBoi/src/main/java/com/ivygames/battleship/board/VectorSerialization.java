package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class VectorSerialization {
    private static final String X = "X";
    private static final String Y = "Y";

    @NonNull
    public static JSONObject toJson(@NonNull Vector coordinate) {
        JSONObject json = new JSONObject();
        try {
            json.put(X, coordinate.x);
            json.put(Y, coordinate.y);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    @NonNull
    public static Vector fromJson(@NonNull String json) {
        try {
            return fromJson(new JSONObject(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Vector fromJson(@NonNull JSONObject json) {
        try {
            int x = json.getInt(X);
            int y = json.getInt(Y);
            return Vector.get(x, y);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
