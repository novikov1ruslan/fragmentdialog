package com.ivygames.morskoiboi.ui.view;

import android.graphics.Rect;

import com.ivygames.morskoiboi.model.Board;

public class BoardG {
    public final float[][] lines = new float[(Board.DIMENSION + 1) * 2][4];

    public Rect frame;
}
