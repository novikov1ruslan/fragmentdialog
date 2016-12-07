package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.player.PlayerOpponent;

import java.util.List;
import java.util.Random;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;


public class RussianBot implements Bot {

    @NonNull
    private final Random mRandom;

    public RussianBot(@NonNull Random random) {
        mRandom = random;
    }

    @NonNull
    @Override
    public Vector shoot(@NonNull Board board) {
        List<Vector> hitDecks = BoardUtils.findFreeHitCells(board);
        List<Vector> possibleShots;
        if (hitDecks.size() == 0) {
            possibleShots = BoardUtils.getPossibleShots(board, false);
        } else if (hitDecks.size() == 1) { // there is newly wounded ship
            Vector v = hitDecks.get(0);
            possibleShots = BoardUtils.getPossibleShotsAround(board, v.x, v.y);
        } else { // wounded ship with > 1 decks hit
            possibleShots = BoardUtils.getPossibleShotsLinear(board, hitDecks);
        }

        int possibleShotsSize = possibleShots.size();
        try {
            // TODO: go over all reportException and see if those happen
            return possibleShots.get(mRandom.nextInt(possibleShotsSize));
        } catch (IllegalArgumentException e) {
            if (PlayerOpponent.debug_board != null) {
                reportException(new IllegalArgumentException(PlayerOpponent.debug_board.toString(), e));
            }
            throw e;
        }
    }

}
