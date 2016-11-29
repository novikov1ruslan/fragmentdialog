package com.ivygames.morskoiboi;

import android.content.res.Resources;
import android.graphics.Paint;
import android.support.annotation.NonNull;

public final class PaintFactory {

    public static Paint newStrokePaint(@NonNull Resources res, int colorId, int dimenId) {
        return PaintFactory.newStrokePaint(res, colorId, res.getDimension(dimenId));
    }

    public static Paint newStrokePaint(@NonNull Resources res, int colorId) {
        return PaintFactory.newStrokePaint(res, colorId, 0F);
    }

    private static Paint newStrokePaint(@NonNull Resources res, int colorId, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(res.getColor(colorId));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public static Paint newFillPaint(@NonNull Resources res, int colorId) {
        Paint paint = new Paint();
        paint.setColor(res.getColor(colorId));
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

}
