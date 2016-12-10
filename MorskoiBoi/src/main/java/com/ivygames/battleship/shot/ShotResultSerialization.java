package com.ivygames.battleship.shot;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.CellSerialization;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.board.VectorSerialization;
import com.ivygames.battleship.ship.ShipSerialization;

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
            Cell cell = CellSerialization.parse((char) jsonObject.getInt(CELL));

            JSONObject aimJson = jsonObject.getJSONObject(AIM);
            Vector aim = VectorSerialization.fromJson(aimJson);

            if (jsonObject.has(SHIP)) {
                JSONObject shipJson = jsonObject.getJSONObject(SHIP);
                return new ShotResult(aim, cell, ShipSerialization.fromJson(shipJson));
            }

            return new ShotResult(aim, cell);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JSONObject toJson(@NonNull ShotResult result) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CELL, CellSerialization.toChar(result.cell));
            jsonObject.put(AIM, VectorSerialization.toJson(result.aim));

            if (result.locatedShip != null) {
                jsonObject.put(SHIP, ShipSerialization.toJson(result.locatedShip));
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }

        return jsonObject;
    }
}
