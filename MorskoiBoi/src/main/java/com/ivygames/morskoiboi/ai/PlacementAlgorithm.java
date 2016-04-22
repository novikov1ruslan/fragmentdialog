package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public interface PlacementAlgorithm {

    /**
     * @return board with full generated fleet placed on it
     */
    Board generateBoard(@NonNull Board board, @NonNull Collection<Ship> ships);

    void putShipAt(Board board, Ship ship, int x, int y);

}
