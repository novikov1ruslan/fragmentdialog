package com.ivygames.morskoiboi.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.InfoCroutonLayout;

public final class UiUtils {

    private UiUtils() {
        // utility class
    }

    private static final Paint sAuxPaint = new Paint();

    static {
        sAuxPaint.setStyle(Paint.Style.STROKE);
    }

    public static Paint newStrokePaint(Resources res, int colorId, int dimenId) {
        return UiUtils.newStrokePaint(res, colorId, res.getDimension(dimenId));
    }

    public static Paint newStrokePaint(Resources res, int colorId) {
        return UiUtils.newStrokePaint(res, colorId, 0F);
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

    public static InfoCroutonLayout inflateInfoCroutonLayout(LayoutInflater inflater, CharSequence message, ViewGroup root) {
        InfoCroutonLayout infoCroutonLayout = (InfoCroutonLayout) inflater.inflate(R.layout.info_crouton, root, false);
        infoCroutonLayout.setMessage(message);
        return infoCroutonLayout;
    }

    public static InfoCroutonLayout inflateChatCroutonLayout(LayoutInflater inflater, CharSequence message, ViewGroup root) {
        InfoCroutonLayout infoCroutonLayout = (InfoCroutonLayout) inflater.inflate(R.layout.chat_crouton, root, false);
        infoCroutonLayout.setMessage(message);
        return infoCroutonLayout;
    }

    /**
     * Determines whether or not the device has an extra large screen.
     *
     * @param context The Android context.
     * @return boolean value indicating if the screen size is extra large.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExtraLargeScreen(Context context) {
        int screenSizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSizeMask == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    public static int getRelativeTop(View myView) {
        if (myView.getParent().getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}
