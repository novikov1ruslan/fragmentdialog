package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public interface PlacementAlgorithm {

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
