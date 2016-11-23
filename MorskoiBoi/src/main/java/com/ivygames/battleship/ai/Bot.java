package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coord;

public interface Bot {

    /**
     * analyzes the board, chooses which cell to shoot, and marks it either miss or hit
     */
    @NonNull
    Coord shoot(@NonNull Board board);
}
