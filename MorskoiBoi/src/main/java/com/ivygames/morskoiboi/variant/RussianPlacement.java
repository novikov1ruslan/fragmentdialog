package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Random;


public final class RussianPlacement extends AbstractPlacement {

    public RussianPlacement(Random random) {
        super(random);
    }

    @Override
    protected void markAdjacentCellsIfNeeded(Ship ship, Cell cell) {
        if (ship.isDead()) {
            cell.setMiss();
        } else {
            cell.setReserved();
        }
    }

}
