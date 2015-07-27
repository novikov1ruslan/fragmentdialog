package com.ivygames.morskoiboi;

import android.graphics.Bitmap;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public interface Rules {
    boolean isBoardSet(Board board);

    boolean isCellConflicting(Cell cell);

    /**
     * @return true if board has 10 ships and all of them are destroyed
     */
    boolean isItDefeatedBoard(Board board);

    int[] getTotalShips();

    int calcTotalScores(Collection<Ship> ships, Game game);

    Bitmap getBitmapForSize(int size);
}
