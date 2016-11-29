package com.ivygames.morskoiboi;

import android.graphics.Paint;

public final class PaintFactory {

    public static Paint newStrokePaint(int rgb) {
        return PaintFactory.newStrokePaint(rgb, 0F);
    }

    public static Paint newStrokePaint(int rgb, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(rgb);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public static Paint newFillPaint(int rgb) {
        Paint paint = new Paint();
        paint.setColor(rgb);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

}
