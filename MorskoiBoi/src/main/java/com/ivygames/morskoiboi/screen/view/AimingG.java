package com.ivygames.morskoiboi.screen.view;

import android.graphics.Rect;

public class AimingG {
    public Rect vertical;
    public Rect horizontal;

    public AimingG(Rect vertical, Rect horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    public AimingG() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AimingG aiming = (AimingG) o;

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
        return "[v=" + vertical + ", h=" + horizontal + "]";
    }
}
