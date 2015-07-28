package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;


public class RussianPlacement extends AbstractPlacement {

    private static volatile long sRandomCounter;

    @Override
    protected void markAdjacentCellsIfNeeded(Ship ship, Cell cell) {
        if (ship.isDead()) {
            cell.setMiss();
        } else {
            cell.setReserved();
        }
    }

}
