package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

// TODO: write tests
public class ShipSerialization {
    private static final String HEALTH = "health";
    private static final String Y = "y";
    private static final String X = "x";
    private static final String IS_HORIZONTAL = "is_horizontal";
    private static final String SIZE = "size";

    @NonNull
    public static JSONObject toJson(@NonNull Ship ship) {
        JSONObject shipJson = new JSONObject();
        try {
            shipJson.put(SIZE, ship.size);
            shipJson.put(IS_HORIZONTAL, ship.isHorizontal());
            shipJson.put(X, ship.mX);
            shipJson.put(Y, ship.mY);
            shipJson.put(HEALTH, ship.mHealth);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return shipJson;
    }

    @NonNull
    public static Board.LocatedShip fromJson(@NonNull String json) {
        try {
            return fromJson(new JSONObject(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Board.LocatedShip fromJson(@NonNull JSONObject json) {
        try {
            Ship ship = new Ship(json.getInt(SIZE));
            ship.mOrientation = json.getBoolean(IS_HORIZONTAL) ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
            int i = json.getInt(X);
            int j = json.getInt(Y);
            ship.mHealth = json.getInt(HEALTH);
            return new Board.LocatedShip(ship, Vector2.get(i, j));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
