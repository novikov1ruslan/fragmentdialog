package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.VectorUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.player.PlayerOpponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;


public class RussianBot implements Bot {

    @NonNull
    private final Random mRandom;

    public RussianBot(@NonNull Random random) {
        mRandom = random;
    }

    private static boolean isEmptyCell(@NonNull Board board, int x, int y) {
        return BoardUtils.contains(x, y) && board.getCell(x, y) == Cell.EMPTY;
    }

    @NonNull
    private static List<Vector> getPossibleShotsAround(@NonNull Board board, int x, int y) {
        ArrayList<Vector> possibleShots = new ArrayList<>();
        if (isEmptyCell(board, x - 1, y)) {
            possibleShots.add(Vector.get(x - 1, y));
        }
        if (isEmptyCell(board, x + 1, y)) {
            possibleShots.add(Vector.get(x + 1, y));
        }
        if (isEmptyCell(board, x, y - 1)) {
            possibleShots.add(Vector.get(x, y - 1));
        }
        if (isEmptyCell(board, x, y + 1)) {
            possibleShots.add(Vector.get(x, y + 1));
        }

        return possibleShots;
    }

    @NonNull
    private static List<Vector> getPossibleShotsLinear(@NonNull Board board,
                                                       @NonNull @Size(min = 2) Collection<Vector> hitDecks) {
        List<Vector> possibleShots = new ArrayList<>();
        Iterator<Vector> iterator = hitDecks.iterator();
        Vector first = iterator.next();
        int minX = first.x;
        int minY = first.y;
        int maxX = first.x;
        int maxY = first.y;
        while (iterator.hasNext()) {
            Vector v = iterator.next();
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

        if (minY == maxY) { // horizontal orientation
            addCellIfEmpty(board, --minX, minY, possibleShots);
            addCellIfEmpty(board, ++maxX, minY, possibleShots);
        } else {
            addCellIfEmpty(board, minX, --minY, possibleShots);
            addCellIfEmpty(board, minX, ++maxY, possibleShots);
        }

        return possibleShots;
    }

    private static void addCellIfEmpty(@NonNull Board board, int x, int y, @NonNull Collection<Vector> out) {
        if (BoardUtils.contains(x, y)) {
            if (board.getCell(x, y) == Cell.EMPTY) {
                out.add(Vector.get(x, y));
            }
        }
    }

    @NonNull
    @Override
    public Vector shoot(@NonNull Board board) {
        Collection<Vector> hitDecks = BoardUtils.findFreeHitCells(board);
        List<Vector> possibleShots;
        if (hitDecks.size() == 0) {
            possibleShots = BoardUtils.getPossibleShots(board, false);
        } else if (hitDecks.size() == 1) { // there is newly wounded ship
            Vector v = VectorUtils.first(hitDecks);
            possibleShots = getPossibleShotsAround(board, v.x, v.y);
        } else { // wounded ship with > 1 decks hit
            possibleShots = getPossibleShotsLinear(board, hitDecks);
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

    private static Object get(@NonNull Collection collection) {
        return collection.iterator().next();
    }

}
