package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

public enum Cell {
    EMPTY(Cell.EMPTY_VAL),
    // TODO: this is not really needed
    RESERVED(Cell.RESERVED_VAL),
    MISS(Cell.MISS_VAL),
    HIT(Cell.HIT_VAL);

    private final static char EMPTY_VAL = ' ';
    private final static char RESERVED_VAL = '0';
    private final static char MISS_VAL = '*';
    private final static char HIT_VAL = 'X';

    private final char mState;

    Cell(char c) {
        mState = c;
    }

    /**
     * Old versions (< 1.4.21) can send boards with proximity value.
     * To support old versions this constant is preserved.
     */
    private static final int LEGACY_RESERVED_PROXIMITY_VALUE = 8;


    @NonNull
    public static Cell parse(char c) {
        switch (c) {
            case RESERVED_VAL:
                return RESERVED;
            case MISS_VAL:
                return MISS;
            case HIT_VAL:
                return HIT;
            case EMPTY_VAL:
                return EMPTY;
            default:
                return Cell.parseProximityCell(c);
        }
    }

    @NonNull
    private static Cell parseProximityCell(char c) {
        int zero = '0';
        if (c >= zero && c <= zero + LEGACY_RESERVED_PROXIMITY_VALUE) {
            return RESERVED;
        } else {
            throw new IllegalArgumentException(Character.toString(c));
        }
    }

    public char toChar() {
        return mState;
    }

    @Override
    public String toString() {
        return "[" + mState + "]";
    }

}
