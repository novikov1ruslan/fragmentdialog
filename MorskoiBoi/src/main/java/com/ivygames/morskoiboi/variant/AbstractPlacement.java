package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.Collection;
import java.util.List;
import java.util.Random;


public abstract class AbstractPlacement implements PlacementAlgorithm {

    private final Random mRandom;

    public AbstractPlacement() {
        mRandom = new Random(System.currentTimeMillis());
    }

    @Override
    public Board generateBoard() {
        Board board = new Board();

        Collection<Ship> ships = GameUtils.generateFullFleet();
        for (Ship ship : ships) {
            place(ship, board);
        }

        return board;
    }

    @Override
    public boolean place(Ship ship, Board board) {
        List<Vector2> cells = board.getEmptyCells();
        boolean found = false;

        while (!cells.isEmpty()) {
            int cellIndex = mRandom.nextInt(cells.size());
            Vector2 cell = cells.get(cellIndex);
            int i = cell.getX();
            int j = cell.getY();
            found = board.canPutShipAt(ship, i, j);
            if (found) {
                found = GameUtils.isPlaceEmpty(ship, board, i, j);
                if (found) {
                    putShipAt(board, ship, i, j);
                    break;
                } else {
                    // this cell is not suitable for placement
                    cells.remove(cellIndex);
                }
            }
        }

        return found;
    }

    @Override
    public void putShipAt(Board board, Ship ship, int x, int y) {
        if (!board.canPutShipAt(ship, x, y)) {
            throw new IllegalArgumentException("cannot put ship " + ship + " at (" + x + "," + y + ")");
        }

        // TODO: if it is exactly the same ship, remove and put again
        ship.setCoordinates(x, y);

        boolean horizontal = ship.isHorizontal();
        for (int i = -1; i <= ship.getSize(); i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                if (board.containsCell(cellX, cellY)) {
                    Cell cell = board.getCell(cellX, cellY);
                    if (ship.isInShip(cellX, cellY)) {
                        cell.addShip();
                        if (ship.isDead()) {
                            cell.setHit();
                        }
                    } else {
                        markAdjacentCellsIfNeeded(ship, cell);
                    }
                }
            }
        }

        board.getShips().add(ship);
    }

    protected void markAdjacentCellsIfNeeded(Ship ship, Cell cell) {
    }

}
