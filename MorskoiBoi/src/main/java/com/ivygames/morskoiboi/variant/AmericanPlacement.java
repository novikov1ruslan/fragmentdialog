package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Random;

public final class AmericanPlacement extends AbstractPlacement {

    public AmericanPlacement(Random random) {
        super(random);
    }

    protected final void markAdjacentCellsIfNeeded(Ship ship, Cell cell) {
    }
}
