package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

final class BotFactory {

    private BotFactory() {
        // factory
    }

    static BotAlgorithm getAlgorithm() {
        return new BotImplementation();
    }

    private static class BotImplementation implements BotAlgorithm {

        private final Random mRandom;
        private final CopyOnWriteArrayList<Vector2> mHitDecks;

        public BotImplementation() {
            mRandom = new Random(System.currentTimeMillis());
            mHitDecks = new CopyOnWriteArrayList<Vector2>();
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

        private boolean isEmptyCell(Board board, int x, int y) {
            return board.containsCell(x, y) && board.getCell(x, y).isEmpty();
        }

        private List<Vector2> getPossibleShotsAround(Board board, int x, int y) {
            ArrayList<Vector2> possibleShots = new ArrayList<Vector2>();
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

        private void addCellIfEmpty(Board board, int x, int y, Collection<Vector2> out) {
            if (board.containsCell(x, y)) {
                Cell cell = board.getCell(x, y);
                if (cell.isEmpty()) {
                    out.add(Vector2.get(x, y));
                }
            }
        }

        private List<Vector2> getPossibleShotsLinear(Board board, List<Vector2> hitCells) {
            List<Vector2> possibleShots = new ArrayList<Vector2>();
            int minX = hitCells.get(0).getX();
            int minY = hitCells.get(0).getY();
            int maxX = hitCells.get(0).getX();
            int maxY = hitCells.get(0).getY();
            for (Vector2 v : hitCells) {
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

            if (GameUtils.areCellsHorizontal(hitCells)) { // TODO: miny == maxy
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
            List<Vector2> possibleShots;
            if (mHitDecks.size() == 0) {
                possibleShots = board.getEmptyCells();
            } else if (mHitDecks.size() == 1) { // there is newly wounded ship
                Vector2 v = mHitDecks.get(0);
                possibleShots = getPossibleShotsAround(board, v.getX(), v.getY());
            } else { // wounded ship with > 1 decks hit
                possibleShots = getPossibleShotsLinear(board, mHitDecks);
            }

            int possibleShotsSize = possibleShots.size();
            try {
                return possibleShots.get(mRandom.nextInt(possibleShotsSize));
            } catch (IllegalArgumentException e) {
                if (PlayerOpponent.debug_board != null) {
                    ACRA.getErrorReporter().handleException(new IllegalArgumentException(PlayerOpponent.debug_board.toString(), e));
                }
                throw e;
            }
        }

        @Override
        public boolean needThinking() {
            return mHitDecks.size() < 2;
        }
    }

}
