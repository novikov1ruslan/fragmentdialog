package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ShipUtils;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Utils {
    static List<Vector2> getShots(@NonNull Rules rules, @NonNull Random random) {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(rules.getAllShipsSizes(), new ShipUtils.OrientationBuilder(random));
        new Placement(random, rules.allowAdjacentShips()).populateBoardWithShips(board, ships);

        List<Vector2> shots = new ArrayList<>(20);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board.getShipsAt(i, j).size() > 0) {
                    shots.add(Vector2.get(i, j));
                }
            }
        }

        return shots;
    }
}
