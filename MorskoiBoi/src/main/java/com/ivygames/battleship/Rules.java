package com.ivygames.battleship;

import android.support.annotation.NonNull;

public interface Rules {
    @NonNull
    int[] getAllShipsSizes();

    boolean allowAdjacentShips();
}
