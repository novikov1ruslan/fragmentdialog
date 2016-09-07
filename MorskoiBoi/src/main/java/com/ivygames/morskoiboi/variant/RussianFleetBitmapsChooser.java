package com.ivygames.morskoiboi.variant;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;

public class RussianFleetBitmapsChooser implements FleetBitmaps {

    @Override
    public Bitmap getSideBitmapForShipSize(@NonNull Resources resources, int size) {
        switch (size) {
            case 4:
                return Bitmaps.getBitmap(resources, R.drawable.aircraft_carrier);
            case 3:
                return Bitmaps.getBitmap(resources, R.drawable.battleship);
            case 2:
                return Bitmaps.getBitmap(resources, R.drawable.frigate);
            case 1:
            default:
                return Bitmaps.getBitmap(resources, R.drawable.gunboat);
        }
    }

    @Override
    public Bitmap getTopBitmapForShipSize(@NonNull Resources resources, int size) {
        switch (size) {
            case 4:
                return Bitmaps.getBitmap(resources, R.drawable._4_square_ship);
            case 3:
                return Bitmaps.getBitmap(resources, R.drawable._3_square_ship);
            case 2:
                return Bitmaps.getBitmap(resources, R.drawable._2_square_ship);
            case 1:
            default:
                return Bitmaps.getBitmap(resources, R.drawable._1_square_ship);
        }
    }
}
