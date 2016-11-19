package com.ivygames.morskoiboi.variant;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;


public class RussianBot implements BotAlgorithm {

    @NonNull
    private final Random mRandom;

    public RussianBot(@NonNull Random random) {
        mRandom = random;
    }

    /**
     * @return true if coordinates {@code vector} are aligned horizontally
     */
    private static boolean coordinatesAlignedHorizontally(@NonNull @Size(min = 1) List<Vector2> coordinates) {

        int y = coordinates.get(0).getY();

        for (int i = 1; i < coordinates.size(); i++) {
            if (y != coordinates.get(i).getY()) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    @Override
    public Vector2 shoot(@NonNull Board board) {
        // TODO: this method does not change the board. Add immutable board and pass it for correctness
        List<Vector2> hitDecks = findHitCells(board);
        List<Vector2> possibleShots;
        if (hitDecks.size() == 0) {
            possibleShots = BoardSetupUtils.getPossibleShots(board, false);
        } else if (hitDecks.size() == 1) { // there is newly wounded ship
            Vector2 v = hitDecks.get(0);
            possibleShots = getPossibleShotsAround(board, v.getX(), v.getY());
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

    private static boolean isEmptyCell(@NonNull Board board, int x, int y) {
        return Board.contains(x, y) && board.getCell(x, y) == Cell.EMPTY;
    }

    @NonNull
    public static List<Vector2> getPossibleShotsAround(@NonNull Board board, int x, int y) {
        ArrayList<Vector2> possibleShots = new ArrayList<>();
        if (isEmptyCell(board, x - 1, y)) {
            possibleShots.add(Vector2.get(x - 1, y));
        }
        if (isEmptyCell(board, x + 1, y)) {
            possibleShots.add(Vector2.get(x + 1, y));
        }
        if (isEmptyCell(board, x, y - 1)) {
            possibleShots.add(Vector2.get(x, y - 1));
        }
        if (isEmptyCell(board, x, y + 1)) {
            possibleShots.add(Vector2.get(x, y + 1));
        }

        return possibleShots;
    }

    private static void addCellIfEmpty(@NonNull Board board, int x, int y, @NonNull Collection<Vector2> out) {
        if (Board.contains(x, y)) {
            Cell cell = board.getCell(x, y);
            if (cell == Cell.EMPTY) {
                out.add(Vector2.get(x, y));
            }
        }
    }

    @NonNull
    private static List<Vector2> getPossibleShotsLinear(@NonNull Board board, @NonNull List<Vector2> hitDecks) {
        List<Vector2> possibleShots = new ArrayList<>();
        int minX = hitDecks.get(0).getX();
        int minY = hitDecks.get(0).getY();
        int maxX = hitDecks.get(0).getX();
        int maxY = hitDecks.get(0).getY();
        for (Vector2 v : hitDecks) {
            int x = v.getX();
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }

            int y = v.getY();
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
    private static List<Vector2> findHitCells(@NonNull Board board) {
        List<Vector2> decks = new ArrayList<>();
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                if (board.getCell(i, j) == Cell.HIT) {
                    if (board.getShipsAt(i, j).isEmpty()) {
                        decks.add(Vector2.get(i, j));
                    }
                }
            }
        }
        return decks;
    }

}
