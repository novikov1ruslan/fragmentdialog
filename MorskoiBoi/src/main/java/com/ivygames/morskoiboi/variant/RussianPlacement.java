package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public class RussianPlacement extends AbstractPlacement {

    private static volatile long sRandomCounter;

    @Override
    public Collection<Ship> generateFullFleet() {
        return GameUtils.generateFullFleet(new int[] {4, 3, 3, 2, 2, 2, 1, 1, 1, 1});
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
