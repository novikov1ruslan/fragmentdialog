package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

interface BotAlgorithm {

    /**
     * analyzes the board, chooses which cell to shoot, and marks it either miss or hit
     */
    Vector2 shoot(Board board);

    boolean needThinking();

    void setLastResult(PokeResult result);
}
