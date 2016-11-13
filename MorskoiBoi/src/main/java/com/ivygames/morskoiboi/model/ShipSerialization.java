package com.ivygames.morskoiboi.model;

import org.json.JSONException;
import org.json.JSONObject;

// TODO: write tests
public class ShipSerialization {
    private static final String HEALTH = "health";
    private static final String Y = "y";
    private static final String X = "x";
    private static final String IS_HORIZONTAL = "is_horizontal";
    private static final String SIZE = "size";

    public static JSONObject toJson(Ship ship) {
        JSONObject shipJson = new JSONObject();
        try {
            shipJson.put(SIZE, ship.getSize());
            shipJson.put(IS_HORIZONTAL, ship.isHorizontal());
            shipJson.put(X, ship.mX);
            shipJson.put(Y, ship.mY);
            shipJson.put(HEALTH, ship.mHealth);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return shipJson;
    }

    public static Ship fromJson(String json) {
        try {
            return fromJson(new JSONObject(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Ship fromJson(JSONObject json) {
        try {
            Ship ship = new Ship(json.getInt(SIZE));
            ship.mOrientation = json.getBoolean(IS_HORIZONTAL) ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
            ship.setX(json.getInt(X));
            ship.setY(json.getInt(Y));
            ship.mHealth = json.getInt(HEALTH);
            return ship;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
