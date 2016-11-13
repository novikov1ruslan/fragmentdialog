package com.ivygames.morskoiboi.model;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class ShotResult {

    private static final String CELL = "CELL";
    private static final String SHIP = "SHIP";
    private static final String AIM = "AIM";

    public final Cell cell;
    public final Ship ship;
    public final Vector2 aim;

    public static ShotResult fromJson(String json) {
        Cell cell;
        Ship ship = null;
        Vector2 aim = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            cell = Cell.parse((char) jsonObject.getInt(CELL));

            if (jsonObject.has(SHIP)) {
                JSONObject shipJson = jsonObject.getJSONObject(SHIP);
                ship = ShipSerialization.fromJson(shipJson);
            }
            if (jsonObject.has(AIM)) {
                JSONObject aimJson = jsonObject.getJSONObject(AIM);
                aim = Vector2.fromJson(aimJson);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return new ShotResult(aim, cell, ship);
    }

    // TODO: unit test
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CELL, cell.toChar());

            if (ship != null) {
                jsonObject.put(SHIP, ShipSerialization.toJson(ship));
            }

            jsonObject.put(AIM, aim.toJson());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    // TODO: ship can have different coordinates than aim - this is a bug
    public ShotResult(Vector2 aim, Cell cell, @Nullable Ship ship) {
        this.cell = cell;
        this.ship = ship;
        this.aim = aim;
    }

    public ShotResult(Vector2 aim, Cell cell) {
        this.cell = cell;
        this.ship = null;
        this.aim = aim;
    }

    @Override
    public String toString() {
        return aim + "; " + cell + "; " + (ship == null ? "" : ship.toString());
    }
}
