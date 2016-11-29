package com.ivygames.battleship;

import android.support.annotation.NonNull;

import com.ivygames.common.DebugUtils;

public class RussianRules implements Rules {

    @NonNull
    @Override
    public int[] getAllShipsSizes() {
        return new int[]{4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    }

    @Override
    public boolean allowAdjacentShips() {
        return false;
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
