package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.player.PlayerOpponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;


public class RussianBot implements Bot {

    @NonNull
    private final Random mRandom;

    public RussianBot(@NonNull Random random) {
        mRandom = random;
    }

    /**
     * @return true if coordinates {@code vector} are aligned horizontally
     */
    private static boolean coordinatesAlignedHorizontally(@NonNull @Size(min = 1) List<Vector> coordinates) {

        int y = coordinates.get(0).y;

        for (int i = 1; i < coordinates.size(); i++) {
            if (y != coordinates.get(i).y) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    @Override
    public Vector shoot(@NonNull Board board) {
        // TODO: this method does not change the board. Add immutable board and pass it for correctness
        List<Vector> hitDecks = BoardUtils.findHitCells(board);
        List<Vector> possibleShots;
        if (hitDecks.size() == 0) {
            possibleShots = BoardUtils.getPossibleShots(board, false);
        } else if (hitDecks.size() == 1) { // there is newly wounded ship
            Vector v = hitDecks.get(0);
            possibleShots = BoardUtils.getPossibleShotsAround(board, v.x, v.y);
        } else { // wounded ship with > 1 decks hit
            possibleShots = getPossibleShotsLinear(board, hitDecks);
        }

        int possibleShotsSize = possibleShots.size();
        try {
            return possibleShots.get(mRandom.nextInt(possibleShotsSize));
        } catch (IllegalArgumentException e) {
            if (PlayerOpponent.debug_board != null) {
                reportException(new IllegalArgumentException(PlayerOpponent.debug_board.toString(), e));
            }
            throw e;
        }
    }

    private static void addCellIfEmpty(@NonNull Board board, int x, int y, @NonNull Collection<Vector> out) {
        if (BoardUtils.contains(x, y)) {
            Cell cell = board.getCell(x, y);
            if (cell == Cell.EMPTY) {
                out.add(Vector.get(x, y));
            }
        }
    }

    // TODO: move to BoardUtils and test
    @NonNull
    private static List<Vector> getPossibleShotsLinear(@NonNull Board board,
                                                       @NonNull @Size(min = 2) List<Vector> hitDecks) {
        List<Vector> possibleShots = new ArrayList<>();
        int minX = hitDecks.get(0).x;
        int minY = hitDecks.get(0).y;
        int maxX = hitDecks.get(0).x;
        int maxY = hitDecks.get(0).y;
        for (Vector v : hitDecks) {
            int x = v.x;
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }

            int y = v.y;
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        if (coordinatesAlignedHorizontally(hitDecks)) { // TODO: miny == maxy
            addCellIfEmpty(board, --minX, minY, possibleShots);
            addCellIfEmpty(board, ++maxX, minY, possibleShots);
        } else {
            addCellIfEmpty(board, minX, --minY, possibleShots);
            addCellIfEmpty(board, minX, ++maxY, possibleShots);
        }

        return possibleShots;
    }

}
