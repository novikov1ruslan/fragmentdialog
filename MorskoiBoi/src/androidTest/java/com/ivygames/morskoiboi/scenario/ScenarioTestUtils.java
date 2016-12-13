package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Placement;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.RandomOrientationBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

class ScenarioTestUtils {
    static List<Vector> getShots(@NonNull Rules rules, @NonNull Random random) {
        Collection<Ship> ships = ShipUtils.createNewShips(rules.getAllShipsSizes(), new RandomOrientationBuilder(random));
        Board board = new Placement(random, rules.allowAdjacentShips()).newBoardWithShips(ships);

        List<Vector> shots = new ArrayList<>(20);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board.getShipsAt(i, j).size() > 0) {
                    shots.add(Vector.get(i, j));
                }
            }
        }

        return shots;
    }
}
