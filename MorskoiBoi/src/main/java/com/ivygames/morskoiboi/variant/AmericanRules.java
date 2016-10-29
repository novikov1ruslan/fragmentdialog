package com.ivygames.morskoiboi.variant;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;
import java.util.Random;

public abstract class AmericanRules extends AbstractRules {

    private static final int[] TOTAL_SHIPS = {5, 4, 3, 3, 2};

    public AmericanRules(@NonNull Random random) {
        super(random);
    }

    @Override
    public boolean isCellConflicting(@NonNull Cell cell) {
        return cell.isReserved() && cell.getProximity() >= Cell.RESERVED_PROXIMITY_VALUE * 2;
    }

    @NonNull
    @Override
    public int[] getAllShipsSizes() {
        return TOTAL_SHIPS;
    }

    @Override
    public int calcTotalScores(@NonNull Collection<Ship> ships, @NonNull Game.Type type, @NonNull ScoreStatistics statistics, boolean surrendered) {
        // TODO: implement
        return 0;
    }

    public Cell getAdjacentCellForShip(Ship ship) {
        return null;
    }
}
