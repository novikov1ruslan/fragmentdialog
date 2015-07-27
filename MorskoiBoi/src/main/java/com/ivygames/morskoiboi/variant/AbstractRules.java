package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRules implements Rules {

    @Override
    public boolean isBoardSet(Board board) {
        return board.getShips().size() == getTotalShips() && getInvalidCells(board).isEmpty();
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

    /**
     * @return true if board has 10 ships and all of them are destroyed
     */
    @Override
    public boolean isItDefeatedBoard(Board board) {
        return board.getShips().size() == getTotalShips() && board.allAvailableShipsAreDestroyed();
    }
}
