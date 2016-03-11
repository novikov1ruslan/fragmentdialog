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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aiming aiming = (Aiming) o;

        if (vertical != null ? !vertical.equals(aiming.vertical) : aiming.vertical != null)
            return false;
        return horizontal != null ? horizontal.equals(aiming.horizontal) : aiming.horizontal == null;

    }

    @Override
    public int hashCode() {
        int result = vertical != null ? vertical.hashCode() : 0;
        result = 31 * result + (horizontal != null ? horizontal.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Aiming{" +
                "vertical=" + vertical +
                ", horizontal=" + horizontal +
                '}';
    }
}
