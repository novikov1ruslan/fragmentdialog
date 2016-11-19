package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class ShotResult {

    private static final String CELL = "CELL";
    private static final String SHIP = "SHIP";
    private static final String AIM = "AIM";

    @NonNull
    public final Cell cell;
    @NonNull
    public final Vector2 aim;
    @Nullable
    public final Ship ship;

    @NonNull
    public static ShotResult fromJson(@NonNull String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Cell cell = Cell.parse((char) jsonObject.getInt(CELL));

            JSONObject aimJson = jsonObject.getJSONObject(AIM);
            Vector2 aim = Vector2.fromJson(aimJson);

            if (jsonObject.has(SHIP)) {
                JSONObject shipJson = jsonObject.getJSONObject(SHIP);
                Board.LocatedShip locatedShip = ShipSerialization.fromJson(shipJson);
                return new ShotResult(aim, cell, locatedShip.ship);
            }

            return new ShotResult(aim, cell);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: unit test
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CELL, cell.toChar());
            jsonObject.put(AIM, aim.toJson());

            if (ship != null) {
                jsonObject.put(SHIP, ShipSerialization.toJson(ship));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    // TODO: ship can have different coordinates than aim - this is a bug
    public ShotResult(@NonNull Vector2 aim, @NonNull Cell cell, @Nullable Ship ship) {
        this.cell = cell;
        this.ship = ship;
        this.aim = aim;
    }

    public ShotResult(@NonNull Vector2 aim, @NonNull Cell cell) {
        this.cell = cell;
        this.ship = null;
        this.aim = aim;
    }

    @Override
    public String toString() {
        return aim + "; " + cell + "; " + (ship == null ? "" : ship.toString());
    }
}
