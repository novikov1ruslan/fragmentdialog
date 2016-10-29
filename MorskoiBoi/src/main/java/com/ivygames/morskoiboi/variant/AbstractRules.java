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

    @NonNull
    private final Random mRandom;

    protected AbstractRules(@NonNull Random random) {
        mRandom = random;
    }

    @Override
    public boolean isBoardSet(@NonNull Board board) {
        return allShipsAreOnBoard(board) && !isThereConflictingCell(board);
    }

    private boolean allShipsAreOnBoard(@NonNull Board board) {
        return board.getShips().size() == getAllShipsSizes().length;
    }

    private boolean isThereConflictingCell(@NonNull Board board) {
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
    public boolean isItDefeatedBoard(@NonNull Board board) {
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

    @NonNull
    @Override
    public Collection<Ship> generateFullFleet() {
        return generateShipsForSizes(getAllShipsSizes(), mRandom);
    }

    @NonNull
    private static Collection<Ship> generateShipsForSizes(@NonNull int[] allShipsSizes,
                                                          @NonNull Random random) {
        List<Ship> fleet = new ArrayList<>();
        for (int length : allShipsSizes) {
            fleet.add(new Ship(length, calcRandomOrientation(random)));
        }

        return fleet;
    }

    @NonNull
    private static Ship.Orientation calcRandomOrientation(@NonNull Random random) {
        return random.nextInt(2) == 1 ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
    }
}
