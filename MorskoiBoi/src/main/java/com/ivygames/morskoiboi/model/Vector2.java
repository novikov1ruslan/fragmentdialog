package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class Vector2 {
    private static final Vector2[][] POOL = new Vector2[Board.DIMENSION][Board.DIMENSION];
    public static final Vector2 INVALID_VECTOR = new Vector2(-1, -1);

    private static final String X = "X";
    private static final String Y = "Y";

    // TODO: unit test
    @NonNull
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(X, mX);
            json.put(Y, mY);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    @NonNull
    public static Vector2 fromJson(@NonNull String json) {
        try {
            return Vector2.fromJson(new JSONObject(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Vector2 fromJson(@NonNull JSONObject json) {
        try {
            int x = json.getInt(X);
            int y = json.getInt(Y);
            return Vector2.get(x, y);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Vector2(int x, int y) {
        mX = x;
        mY = y;
    }

    static {
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                POOL[i][j] = new Vector2(i, j);
            }
        }
    }

    private final int mX;
    private final int mY;

    private static boolean containsCell(int i, int j) {
        return i < Board.DIMENSION && i >= 0 && j < Board.DIMENSION && j >= 0;
    }

    // TODO: test
    public static Vector2 get(int i, int j) {
        if (!containsCell(i, j)) {
            return INVALID_VECTOR;
        }
        return POOL[i][j];
    }

    public static List<Vector2> getAllCoordinates() {
        ArrayList<Vector2> coordinates = new ArrayList<>(Board.DIMENSION * Board.DIMENSION);
        for (int i = 0; i < Board.DIMENSION; i++) {
            coordinates.addAll(Arrays.asList(POOL[i]));
        }
        return coordinates;
    }

    // TODO: remove getters
    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mX;
        result = prime * result + mY;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vector2 other = (Vector2) obj;
        if (mX != other.mX) {
            return false;
        }
        return mY == other.mY;
    }

    @Override
    public String toString() {
        return "[" + mX + "," + mY + "]";
    }
}
