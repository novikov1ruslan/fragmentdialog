package com.ivygames.morskoiboi;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import org.commons.logger.Ln;

public class Bitmaps {

    private static final int BPP = 4;

    @NonNull
    private static final SparseArray<Bitmap> sBitmaps = new SparseArray<>();

    private static int sMemoryUsed;
    private static FleetBitmaps sFleetBitmapsChooser;

    private Bitmaps() {
    }

    public static void loadBitmaps(@NonNull FleetBitmaps fleetBitmapsChooser,
                                   @NonNull Resources res) {
        sFleetBitmapsChooser = fleetBitmapsChooser;

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

    private static void put(@NonNull Resources res, @DrawableRes int resId) {
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

    public static Bitmap getSideBitmapForShipSize(@NonNull Resources resources, int size) {
        // TODO: what if called before load? NPE
        return sFleetBitmapsChooser.getSideBitmapForShipSize(resources, size);
    }

    public static Bitmap getTopBitmapForShipSize(@NonNull Resources resources, int size) {
        return sFleetBitmapsChooser.getTopBitmapForShipSize(resources, size);
    }

}
