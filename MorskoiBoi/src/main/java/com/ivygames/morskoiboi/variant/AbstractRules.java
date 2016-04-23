package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;

public abstract class AbstractRules implements Rules {

    @Override
    public boolean isBoardSet(Board board) {
        return allShipsAreOnBoard(board) && !isThereConflictingCell(board);
    }

    private boolean allShipsAreOnBoard(Board board) {
        return board.getShips().size() == getAllShipsSizes().length;
    }

    private boolean isThereConflictingCell(Board board) {
        for (int i = 0; i < board.getHorizontalDim(); i++) {
            for (int j = 0; j < board.getVerticalDim(); j++) {
                if (isCellConflicting(board.getCell(i, j))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return true if board has 10 ships and all of them are destroyed
     */
    @Override
    public boolean isItDefeatedBoard(Board board) {
        return allShipsAreOnBoard(board) && Board.allAvailableShipsAreDestroyed(board);
    }

}
