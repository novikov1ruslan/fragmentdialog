package com.ivygames.morskoiboi.renderer;

import android.graphics.Rect;

import com.ivygames.morskoiboi.model.Board;

import java.util.Arrays;

class BoardG {
    public final float[][] lines = new float[(Board.DIMENSION + 1) * 2][4];

    public Rect frame;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardG boardG = (BoardG) o;

        if (!Arrays.deepEquals(lines, boardG.lines)) return false;
        return frame != null ? frame.equals(boardG.frame) : boardG.frame == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(lines);
        result = 31 * result + (frame != null ? frame.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[" +
                "lines=" + Arrays.deepToString(lines) +
                ", frame=" + frame +
                "]";
    }
}
