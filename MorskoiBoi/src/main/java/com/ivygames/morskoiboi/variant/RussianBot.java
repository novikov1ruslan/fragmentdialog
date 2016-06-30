package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;


public class RussianBot implements BotAlgorithm {

    private final Random mRandom;
    private final CopyOnWriteArrayList<Vector2> mHitDecks;

    public RussianBot(Random random) {
        mRandom = random;
        mHitDecks = new CopyOnWriteArrayList<>();
    }

    @Override
    public void setLastResult(PokeResult result) {
        if (result.ship == null) {
            if (result.cell.isHit()) {
                mHitDecks.add(result.aim);
            }
        } else {
            mHitDecks.clear();
        }
    }

    private static boolean isEmptyCell(Board board, int x, int y) {
        return Board.containsCell(x, y) && board.getCell(x, y).isEmpty();
    }

    private static List<Vector2> getPossibleShotsAround(Board board, int x, int y) {
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

    private List<Vector2> getPossibleShotsLinear(Board board) {
        List<Vector2> possibleShots = new ArrayList<>();
        int minX = mHitDecks.get(0).getX();
        int minY = mHitDecks.get(0).getY();
        int maxX = mHitDecks.get(0).getX();
        int maxY = mHitDecks.get(0).getY();
        for (Vector2 v : mHitDecks) {
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

        if (GameUtils.coordinatesAlignedHorizontally(mHitDecks)) { // TODO: miny == maxy
            addCellIfEmpty(board, --minX, minY, possibleShots);
            addCellIfEmpty(board, ++maxX, minY, possibleShots);
        } else {
            addCellIfEmpty(board, minX, --minY, possibleShots);
            addCellIfEmpty(board, minX, ++maxY, possibleShots);
        }

        return possibleShots;
    }

    @Override
    public Vector2 shoot(Board board) {
        // TODO: this method does not change the board. Add immutabe board and pass it for correctness
        List<Vector2> possibleShots;
        if (mHitDecks.size() == 0) {
            possibleShots = board.getEmptyCells();
        } else if (mHitDecks.size() == 1) { // there is newly wounded ship
            Vector2 v = mHitDecks.get(0);
            possibleShots = getPossibleShotsAround(board, v.getX(), v.getY());
        } else { // wounded ship with > 1 decks hit
            possibleShots = getPossibleShotsLinear(board);
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

}
