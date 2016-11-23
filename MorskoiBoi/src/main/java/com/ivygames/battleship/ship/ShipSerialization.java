package com.ivygames.battleship.ship;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Coordinate;
import com.ivygames.battleship.board.LocatedShip;

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
    public static JSONObject toJson(@NonNull LocatedShip locatedShip) {
        JSONObject shipJson = new JSONObject();
        try {
            Ship ship = locatedShip.ship;
            Coordinate coordinate = locatedShip.coordinate;
            shipJson.put(SIZE, ship.size);
            shipJson.put(IS_HORIZONTAL, ship.isHorizontal());
            shipJson.put(X, coordinate.i);
            shipJson.put(Y, coordinate.j);
            shipJson.put(HEALTH, ship.mHealth);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return shipJson;
    }

    @NonNull
    public static LocatedShip fromJson(@NonNull String json) {
        try {
            return fromJson(new JSONObject(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static LocatedShip fromJson(@NonNull JSONObject json) {
        try {
            Ship ship = new Ship(json.getInt(SIZE));
            ship.mOrientation = json.getBoolean(IS_HORIZONTAL) ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
            int i = json.getInt(X);
            int j = json.getInt(Y);
            ship.mHealth = json.getInt(HEALTH);
            return new LocatedShip(ship, Coordinate.get(i, j));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
