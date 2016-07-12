package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Vector2;

public interface BotAlgorithm {

    /**
     * analyzes the board, chooses which cell to shoot, and marks it either miss or hit
     */
    @NonNull
    Vector2 shoot(@NonNull Board board);
}
