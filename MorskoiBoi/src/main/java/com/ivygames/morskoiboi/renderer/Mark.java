package com.ivygames.morskoiboi.renderer;

class Mark {
    public int centerX;
    public int centerY;
    public float outerRadius;
    public float innerRadius;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mark mark = (Mark) o;

        if (centerX != mark.centerX) return false;
        if (centerY != mark.centerY) return false;
        if (Float.compare(mark.outerRadius, outerRadius) != 0) return false;
        return Float.compare(mark.innerRadius, innerRadius) == 0;

    }

    @Override
    public int hashCode() {
        int result = centerX;
        result = 31 * result + centerY;
        result = 31 * result + (outerRadius != +0.0f ? Float.floatToIntBits(outerRadius) : 0);
        result = 31 * result + (innerRadius != +0.0f ? Float.floatToIntBits(innerRadius) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[" +
                "X=" + centerX +
                ", Y=" + centerY +
                ", r2=" + outerRadius +
                ", r1=" + innerRadius +
                ']';
    }
}
