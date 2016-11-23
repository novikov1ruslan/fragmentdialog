package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

public class CellSerialization {
    private final static char EMPTY_VAL = ' ';
    private final static char RESERVED_VAL = '0';
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
            case RESERVED_VAL:
                return Cell.RESERVED;
            case MISS_VAL:
                return Cell.MISS;
            case HIT_VAL:
                return Cell.HIT;
            case EMPTY_VAL:
                return Cell.EMPTY;
            default:
                return parseProximityCell(c);
        }
    }

    @NonNull
    private static Cell parseProximityCell(char c) {
        int zero = '0';
        if (c >= zero && c <= zero + LEGACY_RESERVED_PROXIMITY_VALUE) {
            return Cell.RESERVED;
        } else {
            throw new IllegalArgumentException(Character.toString(c));
        }
    }

    public static char toChar(@NonNull Cell cell) {
        switch (cell) {
            case EMPTY:
                return EMPTY_VAL;
            case MISS:
                return MISS_VAL;
            case HIT:
                return HIT_VAL;
            default:
            case RESERVED:
                return RESERVED_VAL;
        }
    }
}
