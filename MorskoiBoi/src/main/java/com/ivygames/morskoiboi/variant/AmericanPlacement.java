package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Random;

public final class AmericanPlacement extends AbstractPlacement {

    public AmericanPlacement(Random random, Rules rules) {
        super(random, rules);
    }

    protected final void markAdjacentCellsIfNeeded(Ship ship, Cell cell) {
    }
}
