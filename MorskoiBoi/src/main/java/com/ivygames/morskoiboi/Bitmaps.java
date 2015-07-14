package com.ivygames.morskoiboi;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

import org.commons.logger.Ln;

public final class Bitmaps {

    private final SparseArray<Bitmap> mBitmaps = new SparseArray<Bitmap>();

    private int mMemoryUsed;

    private static final Bitmaps INSTANCE = new Bitmaps();

    public static Bitmaps getInstance() {
        return INSTANCE;
    }

    private Bitmaps() {
    }

    public void loadBitmaps(Resources res) {

        put(res, R.drawable.aircraft_carrier);
        put(res, R.drawable.battleship);
        put(res, R.drawable.frigate);
        put(res, R.drawable.gunboat);
        put(res, R.drawable.combo_medal);

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

        Ln.d("memory used by bitmaps: " + (mMemoryUsed / 1024) + "k");
    }

    private void put(Resources res, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
        mBitmaps.put(resId, bitmap);

        mMemoryUsed += bitmap.getHeight() * bitmap.getWidth() * 4;
    }

    public Bitmap getBitmap(int resId) {
        return mBitmaps.get(resId);
    }
}
