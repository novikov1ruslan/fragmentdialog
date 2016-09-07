package com.ivygames.morskoiboi;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.ivygames.morskoiboi.variant.FleetBitmaps;

import org.commons.logger.Ln;

public class Bitmaps {

    private static final int BPP = 4;

    @NonNull
    private static final SparseArray<Bitmap> sBitmaps = new SparseArray<>();

    private static int sMemoryUsed;
    private static FleetBitmaps fleetBitmapsChooser;

    private Bitmaps() {
    }

    public static void loadBitmaps(@NonNull FleetBitmaps fleetBitmapsChooser,
                                   @NonNull Resources res) {
        Bitmaps.fleetBitmapsChooser = fleetBitmapsChooser;

        put(res, R.drawable.aircraft_carrier);
        put(res, R.drawable.battleship);
        put(res, R.drawable.frigate);
        put(res, R.drawable.gunboat);

        put(res, R.drawable.splash_01);
        put(res, R.drawable.splash_02);
        put(res, R.drawable.splash_03);
        put(res, R.drawable.splash_04);
        put(res, R.drawable.splash_05);
        put(res, R.drawable.splash_06);
        put(res, R.drawable.splash_07);
        put(res, R.drawable.splash_08);
        put(res, R.drawable.splash_09);
        put(res, R.drawable.splash_10);

        put(res, R.drawable.explosion_01);
        // put(res, R.drawable.explosion_02);
        put(res, R.drawable.explosion_03);
        // put(res, R.drawable.explosion_04);
        put(res, R.drawable.explosion_05);
        put(res, R.drawable.explosion_06);
        put(res, R.drawable.explosion_07);
        put(res, R.drawable.explosion_08);
        put(res, R.drawable.explosion_09);
        put(res, R.drawable.explosion_10);
        // put(res, R.drawable.explosion_11);
        put(res, R.drawable.explosion_12);
        put(res, R.drawable.explosion_13);
        put(res, R.drawable.explosion_14);
        put(res, R.drawable.explosion_15);
        // put(res, R.drawable.explosion_16);
        put(res, R.drawable.explosion_17);
        put(res, R.drawable.explosion_18);

        put(res, R.drawable._1_square_ship);
        put(res, R.drawable._2_square_ship);
        put(res, R.drawable._3_square_ship);
        put(res, R.drawable._4_square_ship);

        Ln.d("memory used by bitmaps: " + (sMemoryUsed / 1024) + "k");
    }

    private static void put(Resources res, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
        sBitmaps.put(resId, bitmap);

        if (bitmap != null) {
            sMemoryUsed += bitmap.getHeight() * bitmap.getWidth() * BPP;
        }
    }

    public static Bitmap getBitmap(@NonNull Resources res, int resId) {
        Bitmap bitmap = sBitmaps.get(resId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(res, resId);
            if (bitmap != null) {
                Ln.e("bitmap_crash_saved");
                sBitmaps.put(resId, bitmap);
            }
        }

        return bitmap;
    }

    public static Bitmap getSideBitmapForShipSize(Resources resources, int size) {
        return fleetBitmapsChooser.getSideBitmapForShipSize(resources, size);
    }

    public static Bitmap getTopBitmapForShipSize(Resources resources, int size) {
        return fleetBitmapsChooser.getTopBitmapForShipSize(resources, size);
    }

    public int getBitmapSize(Resources res, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap tmp = BitmapFactory.decodeResource(res, resId, options);
        return tmp.getByteCount();
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
