package com.ivygames.morskoiboi.russian;

import android.support.annotation.NonNull;

import com.ivygames.common.DebugUtils;
import com.ivygames.morskoiboi.Rules;

public class RussianRules implements Rules {

    private static final int[] TOTAL_SHIPS = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    @NonNull
    @Override
    public int[] getAllShipsSizes() {
        return TOTAL_SHIPS;
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
