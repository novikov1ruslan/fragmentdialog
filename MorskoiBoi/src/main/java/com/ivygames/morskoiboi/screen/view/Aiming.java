package com.ivygames.morskoiboi.screen.view;

import android.graphics.Rect;

public class Aiming {
    public Rect vertical;
    public Rect horizontal;

    public Aiming(Rect vertical, Rect horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    public Aiming() {

    }

    @Override
    public String toString() {
        return "Aiming{" +
                "vertical=" + vertical +
                ", horizontal=" + horizontal +
                '}';
    }
}
