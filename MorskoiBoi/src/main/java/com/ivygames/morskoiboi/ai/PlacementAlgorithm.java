package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public interface PlacementAlgorithm {

    /**
     * Places {@code ship} on the {@code board} with arbitrary orientation
     *
     * @param ship specifically oriented ship
     * @return true is the {@code ship} was successfully placed on the {@code board}
     */
    boolean place(Ship ship, Board board);

    /**
     * @return board with full generated fleet placed on it
     */
    Board generateBoard();

    void putShipAt(Board board, Ship ship, int x, int y);

    /**
     * @return fleet of ships with random orientation
     */
    Collection<Ship> generateFullFleet();

}
