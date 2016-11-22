package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.ShipSerialization;
import com.ivygames.morskoiboi.screen.boardsetup.BoardUtils;

import org.commons.logger.Ln;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BoardSerialization {
    private static final String CELLS = "cells";
    private static final String SHIPS = "ships";

    @NonNull
    public static Board fromJson(@NonNull String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return fromJson(jsonObject);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @NonNull
    public static Board fromJson(@NonNull JSONObject jsonObject) {
        Board board = new Board();

        try {
            populateCellsFromString(board.mCells, jsonObject.getString(CELLS));
            populateShipsFromJson(board, jsonObject.getJSONArray(SHIPS));
        } catch (JSONException e) {
            Ln.e(e);
            throw new IllegalArgumentException(e);
        }

        return board;
    }

    private static void populateCellsFromString(@NonNull Cell[][] cells, @NonNull String cellsString) {
        int columns = cells.length;
        for (int i = 0; i < columns; i++) {
            int rows = cells[i].length;
            for (int j = 0; j < rows; j++) {
                cells[i][j] = Cell.parse(cellsString.charAt(i * columns + j));
            }
        }
    }

    private static void populateShipsFromJson(@NonNull Board board, @NonNull JSONArray shipsJson) throws JSONException {
        for (int i = 0; i < shipsJson.length(); i++) {
            JSONObject shipJson = shipsJson.getJSONObject(i);
            board.addShip(ShipSerialization.fromJson(shipJson));
        }
    }

    private static String getStringFromCells(@NonNull Cell[][] cells) {
        StringBuilder sb = new StringBuilder(200);

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                sb.append(cells[i][j].toChar());
            }
        }

        return sb.toString();
    }

    @NonNull
    public static JSONObject toJson(@NonNull Board board) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CELLS, getStringFromCells(board.mCells));

            JSONArray shipsJson = new JSONArray();
            for (LocatedShip locatedShip : board.getLocatedShips()) {
                shipsJson.put(ShipSerialization.toJson(locatedShip));
            }
            jsonObject.put(SHIPS, shipsJson);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }
}
