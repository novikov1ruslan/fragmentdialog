package com.ivygames.battleship.board;

public enum Cell {
    EMPTY,
    // TODO: this is not really needed
    RESERVED,
    MISS,
    HIT;

    @Override
    public String toString() {
        return "[" + CellSerialization.toChar(this) + "]";
    }
}
