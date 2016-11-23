package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Vector;

public interface Bot {

    /**
     * analyzes the board, chooses which cell to shoot, and marks it either miss or hit
     */
    @NonNull
    Vector shoot(@NonNull Board board);
}
