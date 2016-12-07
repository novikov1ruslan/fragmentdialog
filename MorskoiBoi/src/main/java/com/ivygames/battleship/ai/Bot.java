package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Vector;

public interface Bot {

    /**
     * Analyzes the board and returns the coordinate to shoot at
     *
     * @param board, immutable board
     * @return the coordinate to shoot at
     */
    @NonNull
    Vector shoot(@NonNull Board board);
}
