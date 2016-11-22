package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

public interface Rules {
    @NonNull
    int[] getAllShipsSizes();

    boolean allowAdjacentShips();
}
