package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.Collection;
import java.util.List;
import java.util.Random;


public class Placement implements PlacementAlgorithm {

    private final Random mRandom;
    private Rules mRules;

    public Placement(Random random, Rules rules) {
        mRandom = random;
        mRules = rules;
    }

    @Override
    public Board generateBoard() {
        Board board = new Board();

        Collection<Ship> ships = GameUtils.generateShipsForSizes(mRules.getAllShipsSizes());
        for (Ship ship : ships) {
            putShipOnBoard(ship, board);
        }

        return board;
    }

    private boolean putShipOnBoard(Ship ship, Board board) {
        List<Vector2> cells = board.getEmptyCells();
        while (!cells.isEmpty()) {
            int cellIndex = mRandom.nextInt(cells.size());
            Vector2 cell = cells.get(cellIndex);
            int i = cell.getX();
            int j = cell.getY();
            if (board.shipFitsTheBoard(ship, i, j)) {
                if (GameUtils.isPlaceEmpty(ship, board, i, j)) {
                    putShipAt(board, ship, i, j);
                    return true;
                } else {
                    // this cell is not suitable for placement
                    cells.remove(cellIndex);
                }
            }
        }

        return false;
    }

    @Override
    public void putShipAt(Board board, Ship ship, int x, int y) {
        if (!board.shipFitsTheBoard(ship, x, y)) {
            throw new IllegalArgumentException("cannot put ship " + ship + " at (" + x + "," + y + ")");
        }

        // TODO: if it is exactly the same ship, remove and put again
        ship.setCoordinates(x, y);

        boolean horizontal = ship.isHorizontal();
        for (int i = -1; i <= ship.getSize(); i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                if (Board.containsCell(cellX, cellY)) {
                    Cell cell = board.getCell(cellX, cellY);
                    if (ship.isInShip(cellX, cellY)) {
                        cell.addShip();
                        if (ship.isDead()) {
                            cell.setHit();
                        }
                    } else {
                        cell = mRules.getAdjacentCellForShip(ship);
                        board.setCell(cell, cellX, cellY);
                    }
                }
            }
        }

        board.getShips().add(ship);
    }

}
