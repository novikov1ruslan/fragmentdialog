package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coord;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.OrientationBuilder;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Utils {
    static List<Coord> getShots(@NonNull Rules rules, @NonNull Random random) {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(rules.getAllShipsSizes(), new OrientationBuilder(random));
        new Placement(random, rules.allowAdjacentShips()).populateBoardWithShips(board, ships);

        List<Coord> shots = new ArrayList<>(20);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board.getShipsAt(i, j).size() > 0) {
                    shots.add(Coord.get(i, j));
                }
            }
        }

        return shots;
    }
}
