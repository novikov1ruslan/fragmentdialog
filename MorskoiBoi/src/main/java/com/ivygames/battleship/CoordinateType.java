package com.ivygames.battleship;

public enum CoordinateType {
    NEAR_SHIP,
    IN_SHIP;

    public boolean isNeighboring() {
        return this == NEAR_SHIP;
    }
}
