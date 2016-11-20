package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class ShotResultSerialization {
    private static final String CELL = "CELL";
    private static final String SHIP = "SHIP";
    private static final String AIM = "AIM";

    @NonNull
    public static ShotResult fromJson(@NonNull String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Cell cell = Cell.parse((char) jsonObject.getInt(CELL));

            JSONObject aimJson = jsonObject.getJSONObject(AIM);
            Vector2 aim = Vector2.fromJson(aimJson);

            if (jsonObject.has(SHIP)) {
                JSONObject shipJson = jsonObject.getJSONObject(SHIP);
                return new ShotResult(aim, cell, ShipSerialization.fromJson(shipJson));
            }

            return new ShotResult(aim, cell);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: unit test
    public static JSONObject toJson(@NonNull ShotResult result) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CELL, result.cell.toChar());
            jsonObject.put(AIM, result.aim.toJson());

            if (result.locatedShip != null) {
                jsonObject.put(SHIP, ShipSerialization.toJson(result.locatedShip));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }
}
