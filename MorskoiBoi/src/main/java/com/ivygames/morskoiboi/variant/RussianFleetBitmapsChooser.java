package com.ivygames.morskoiboi.variant;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;

public class RussianFleetBitmapsChooser implements FleetBitmaps {

    @Override
    public Bitmap getBitmapForShipSize(@NonNull Resources resources, int size) {
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
}
