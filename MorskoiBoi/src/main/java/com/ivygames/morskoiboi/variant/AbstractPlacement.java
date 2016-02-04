package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.RulesFactory;
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

    public AbstractPlacement(Random random) {
        mRandom = random;
    }

    @Override
    public Collection<Ship> generateFullFleet() {
        return GameUtils.generateFullFleet(RulesFactory.getRules().getTotalShips());
    }

    @Override
    public Board generateBoard() {
        Board board = new Board();

        Collection<Ship> ships = generateFullFleet();
        for (Ship ship : ships) {
            place(ship, board);
        }

        return board;
    }

    private boolean place(Ship ship, Board board) {
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

    protected abstract void markAdjacentCellsIfNeeded(Ship ship, Cell cell);

}
