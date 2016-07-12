package com.ivygames.morskoiboi.variant;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class AbstractRules implements Rules {

    private static volatile long sRandomCounter;

    @NonNull
    private static Collection<Ship> generateShipsForSizes(@NonNull int[] allShipsSizes) {
        Random random = new Random(System.currentTimeMillis() + ++sRandomCounter);

        List<Ship> fleet = new ArrayList<>();
        for (int length : allShipsSizes) {
            fleet.add(new Ship(length, calcRandomOrientation(random)));
        }

        return fleet;
    }

    private static Ship.Orientation calcRandomOrientation(Random random) {
        return random.nextInt(2) == 1 ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
    }

    @Override
    public boolean isBoardSet(Board board) {
        return allShipsAreOnBoard(board) && !isThereConflictingCell(board);
    }

    private boolean allShipsAreOnBoard(Board board) {
        return board.getShips().size() == getAllShipsSizes().length;
    }

    private boolean isThereConflictingCell(Board board) {
        for (int i = 0; i < board.horizontalDimension(); i++) {
            for (int j = 0; j < board.verticalDimension(); j++) {
                if (isCellConflicting(board.getCell(i, j))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return true if board has 10 ships and all of them are destroyed
     */
    @Override
    public boolean isItDefeatedBoard(Board board) {
        return allShipsAreOnBoard(board) && Board.allAvailableShipsAreDestroyed(board);
    }

    @Override
    public int calcSurrenderPenalty(@NonNull Collection<Ship> ships) {
        int decksLost = getTotalHealth() - getRemainedHealth(ships);
        return decksLost * Game.SURRENDER_PENALTY_PER_DECK + Game.MIN_SURRENDER_PENALTY;
    }

    private int getTotalHealth() {
        int[] ships = getAllShipsSizes();
        int totalHealth = 0;
        for (int ship : ships) {
            totalHealth += ship;
        }
        return totalHealth;
    }

    private static int getRemainedHealth(@NonNull Collection<Ship> ships) {
        int health = 0;
        for (Ship ship : ships) {
            health += ship.getHealth();
        }
        return health;
    }

    @Override
    public Collection<Ship> generateFullFleet() {
        return generateShipsForSizes(getAllShipsSizes());
    }
}
