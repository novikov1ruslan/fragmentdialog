package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class CoordinateSerialization {
    private static final String X = "X";
    private static final String Y = "Y";

    // TODO: unit test
    @NonNull
    public static JSONObject toJson(@NonNull Coordinate coordinate) {
        JSONObject json = new JSONObject();
        try {
            json.put(X, coordinate.i);
            json.put(Y, coordinate.j);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    @NonNull
    public static Coordinate fromJson(@NonNull String json) {
        try {
            return fromJson(new JSONObject(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Coordinate fromJson(@NonNull JSONObject json) {
        try {
            int x = json.getInt(X);
            int y = json.getInt(Y);
            return Coordinate.get(x, y);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
