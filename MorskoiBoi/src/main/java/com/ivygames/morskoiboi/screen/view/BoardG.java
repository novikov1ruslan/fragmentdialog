package com.ivygames.morskoiboi.screen.view;

import android.graphics.Rect;

import com.ivygames.morskoiboi.model.Board;

import java.util.Arrays;

public class BoardG {
    public final float[][] lines = new float[(Board.DIMENSION + 1) * 2][4];

    public Rect frame;

    @Override
    public String toString() {
        return "BoardG{" +
                "lines=" + Arrays.deepToString(lines) +
                ", frame=" + frame +
                '}';
    }
}
