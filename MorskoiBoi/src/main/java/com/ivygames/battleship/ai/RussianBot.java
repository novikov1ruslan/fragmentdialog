package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Coord;
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
    private static boolean coordinatesAlignedHorizontally(@NonNull @Size(min = 1) List<Coord> coordinates) {

        int y = coordinates.get(0).j;

        for (int i = 1; i < coordinates.size(); i++) {
            if (y != coordinates.get(i).j) {
                return false;
            }
        }

        return true;
    }

    private static boolean isEmptyCell(@NonNull Board board, int x, int y) {
        return BoardUtils.contains(x, y) && board.getCell(x, y) == Cell.EMPTY;
    }

    @NonNull
    private static List<Coord> getPossibleShotsAround(@NonNull Board board, int x, int y) {
        ArrayList<Coord> possibleShots = new ArrayList<>();
        if (isEmptyCell(board, x - 1, y)) {
            possibleShots.add(Coord.get(x - 1, y));
        }
        if (isEmptyCell(board, x + 1, y)) {
            possibleShots.add(Coord.get(x + 1, y));
        }
        if (isEmptyCell(board, x, y - 1)) {
            possibleShots.add(Coord.get(x, y - 1));
        }
        if (isEmptyCell(board, x, y + 1)) {
            possibleShots.add(Coord.get(x, y + 1));
        }

        return possibleShots;
    }

    @NonNull
    @Override
    public Coord shoot(@NonNull Board board) {
        // TODO: this method does not change the board. Add immutable board and pass it for correctness
        List<Coord> hitDecks = findHitCells(board);
        List<Coord> possibleShots;
        if (hitDecks.size() == 0) {
            possibleShots = BoardUtils.getPossibleShots(board, false);
        } else if (hitDecks.size() == 1) { // there is newly wounded ship
            Coord v = hitDecks.get(0);
            possibleShots = getPossibleShotsAround(board, v.i, v.j);
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

    private static void addCellIfEmpty(@NonNull Board board, int x, int y, @NonNull Collection<Coord> out) {
        if (BoardUtils.contains(x, y)) {
            Cell cell = board.getCell(x, y);
            if (cell == Cell.EMPTY) {
                out.add(Coord.get(x, y));
            }
        }
    }

    @NonNull
    private static List<Coord> getPossibleShotsLinear(@NonNull Board board,
                                                      @NonNull @Size(min = 2) List<Coord> hitDecks) {
        List<Coord> possibleShots = new ArrayList<>();
        int minX = hitDecks.get(0).i;
        int minY = hitDecks.get(0).j;
        int maxX = hitDecks.get(0).i;
        int maxY = hitDecks.get(0).j;
        for (Coord v : hitDecks) {
            int x = v.i;
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }

            int y = v.j;
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

    /**
     * Assumption is that the cells will belong to the same ship.
     */
    @NonNull
    private static List<Coord> findHitCells(@NonNull Board board) {
        List<Coord> decks = new ArrayList<>();
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                if (board.getCell(i, j) == Cell.HIT) {
                    if (board.getShipsAt(i, j).isEmpty()) {
                        decks.add(Coord.get(i, j));
                    }
                }
            }
        }
        return decks;
    }

}
