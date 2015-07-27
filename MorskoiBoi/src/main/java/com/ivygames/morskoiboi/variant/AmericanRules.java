package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public class AmericanRules extends AbstractRules {

    private static final int TOTAL_SHIPS = 10;

    @Override
    public boolean isCellConflicting(Cell cell) {
        return cell.getProximity() >= Cell.RESERVED_PROXIMITY_VALUE * 2;
    }

    @Override
    public int getTotalShips() {
        return TOTAL_SHIPS;
    }

    @Override
    public int calcTotalScores(Collection<Ship> ships, Game game) {
        // TODO: implement
        return 0;
    }
}
