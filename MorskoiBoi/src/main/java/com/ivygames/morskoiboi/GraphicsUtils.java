package com.ivygames.morskoiboi;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public final class GraphicsUtils {

    private GraphicsUtils() {
        // utility class
    }

    private static final Paint sAuxPaint = new Paint();

    static {
        sAuxPaint.setStyle(Paint.Style.STROKE);
    }

    public static Paint newStrokePaint(Resources res, int colorId, int dimenId) {
        return GraphicsUtils.newStrokePaint(res, colorId, res.getDimension(dimenId));
    }

    public static Paint newStrokePaint(Resources res, int colorId) {
        return GraphicsUtils.newStrokePaint(res, colorId, 0F);
    }

    private static Paint newStrokePaint(Resources res, int colorId, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(res.getColor(colorId));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        return paint;
    }

    public static Paint newFillPaint(Resources res, int colorId) {
        Paint paint = new Paint();
        paint.setColor(res.getColor(colorId));
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    public static Bitmap invert(Bitmap src) {
        Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int A, R, G, B;
        int pixelColor;
        int height = src.getHeight();
        int width = src.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelColor = src.getPixel(x, y);
                A = Color.alpha(pixelColor);

                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);

                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        src.recycle();

        return output;
    }

}
