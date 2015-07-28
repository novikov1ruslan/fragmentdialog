package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public interface PlacementAlgorithm {

    boolean place(Ship ship, Board board);

    Board generateBoard();

    void putShipAt(Board board, Ship ship, int x, int y);

    Collection<Ship> generateFullFleet();

}
