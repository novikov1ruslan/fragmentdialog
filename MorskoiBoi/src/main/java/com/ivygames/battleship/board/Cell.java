package com.ivygames.battleship.board;

public enum Cell {
    EMPTY,
    MISS,
    HIT;

    @Override
    public String toString() {
        return "[" + CellSerialization.toChar(this) + "]";
    }
}
