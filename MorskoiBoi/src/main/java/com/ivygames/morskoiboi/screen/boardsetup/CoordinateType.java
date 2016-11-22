package com.ivygames.morskoiboi.screen.boardsetup;

public enum CoordinateType {
    NEAR_SHIP,
    IN_SHIP;

    public boolean isNeighboring() {
        return this == NEAR_SHIP;
    }
}
