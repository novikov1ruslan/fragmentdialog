package com.ivygames.morskoiboi;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public interface Rules {
    boolean isBoardSet(Board board);

    boolean isCellConflicting(Cell cell);

    /**
     * @return true if board has full fleet and all the ships are destroyed
     */
    boolean isItDefeatedBoard(Board board);

    int[] getAllShipsSizes();

    int calcTotalScores(@NonNull Collection<Ship> ships, @NonNull Game game, boolean surrendered);

    Bitmap getBitmapForShipSize(int size);

    Cell setAdjacentCellForShip(@NonNull Ship ship, @NonNull Cell cell);

}
