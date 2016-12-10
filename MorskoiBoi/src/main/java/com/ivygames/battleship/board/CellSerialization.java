package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

public class CellSerialization {
    private final static char EMPTY_VAL = ' ';
    private final static char MISS_VAL = '*';
    private final static char HIT_VAL = 'X';

    /**
     * Old versions (< 1.4.21) can send boards with proximity value.
     * To support old versions this constant is preserved.
     */
    private static final int LEGACY_RESERVED_PROXIMITY_VALUE = 8;

    @NonNull
    public static Cell parse(char c) {
        switch (c) {
            case MISS_VAL:
                return Cell.MISS;
            case HIT_VAL:
                return Cell.HIT;
            case EMPTY_VAL:
            default:
                return Cell.EMPTY;
        }
    }

    public static char toChar(@NonNull Cell cell) {
        switch (cell) {
            case MISS:
                return MISS_VAL;
            case HIT:
                return HIT_VAL;
            case EMPTY:
            default:
                return EMPTY_VAL;
        }
    }
}
