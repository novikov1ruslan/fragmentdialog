package com.ivygames.morskoiboi.variant;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;


public class RussianBot implements BotAlgorithm {

    private final Random mRandom;

    public RussianBot(Random random) {
        mRandom = random;
    }

    @NonNull
    @Override
    public Vector2 shoot(@NonNull Board board) {
        // TODO: this method does not change the board. Add immutable board and pass it for correctness
        List<Vector2> hitDecks = createDecks(board);
        List<Vector2> possibleShots;
        if (hitDecks.size() == 0) {
            possibleShots = board.getEmptyCells();
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
        return Board.containsCell(x, y) && board.getCell(x, y).isEmpty();
    }

    @NonNull
    private static List<Vector2> getPossibleShotsAround(@NonNull Board board, int x, int y) {
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

    private static void addCellIfEmpty(Board board, int x, int y, Collection<Vector2> out) {
        if (Board.containsCell(x, y)) {
            Cell cell = board.getCell(x, y);
            if (cell.isEmpty()) {
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

        if (GameUtils.coordinatesAlignedHorizontally(hitDecks)) { // TODO: miny == maxy
            addCellIfEmpty(board, --minX, minY, possibleShots);
            addCellIfEmpty(board, ++maxX, minY, possibleShots);
        } else {
            addCellIfEmpty(board, minX, --minY, possibleShots);
            addCellIfEmpty(board, minX, ++maxY, possibleShots);
        }

        return possibleShots;
    }

    private static List<Vector2> createDecks(@NonNull Board board) {
        List<Vector2> decks = new ArrayList<>();
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Cell cell = board.getCell(i, j);
                if (cell.isHit()) {
                    if (board.getShipsAt(i, j).isEmpty()) {
                        decks.add(Vector2.get(i, j));
                    }
                }
            }
        }
        return decks;
    }

}
