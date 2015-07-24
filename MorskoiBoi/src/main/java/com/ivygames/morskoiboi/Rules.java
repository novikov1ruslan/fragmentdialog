package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;

public interface Rules {
    boolean isBoardSet(Board board);

    boolean isCellConflicting(Cell cell);
}
