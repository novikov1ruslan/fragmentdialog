package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.HashSet;
import java.util.Set;

public class RussianRules implements Rules {

    public boolean isBoardSet(Board board) {
        return board.getShips().size() == 10 && getInvalidCells(board).isEmpty();
    }

    @Override
    public boolean isCellConflicting(Cell cell) {
        return cell.getProximity() > 8;
    }

    /**
     * Finds all the cells on the board that are in a conflict with another cell.
     */
    private Set<Vector2> getInvalidCells(Board board) {
        Set<Vector2> invalid = new HashSet<Vector2>();
        for (int i = 0; i < board.getHorizontalDim(); i++) {
            for (int j = 0; j < board.getVerticalDim(); j++) {
                if (isCellConflicting(board.getCell(i, j))) {
                    invalid.add(Vector2.get(i, j));
                }
            }
        }

        return invalid;
    }
}
