package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;

public interface PlacementAlgorithm {

	boolean place(Ship ship, Board board);

	Board generateBoard();
}
