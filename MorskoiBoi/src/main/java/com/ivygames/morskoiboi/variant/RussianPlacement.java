package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.Collection;
import java.util.List;
import java.util.Random;


public class RussianPlacement extends AbstractPlacement {


    @Override
    protected void markAdjacentCellsIfNeeded(Ship ship, Cell cell) {
        if (ship.isDead()) {
            cell.setMiss();
        } else {
            cell.setReserved();
        }
    }
}
