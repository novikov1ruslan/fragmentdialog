package com.ivygames.morskoiboi.variant;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;

public abstract class AmericanFleetBitmapsChooser implements FleetBitmaps {
    @Override
    public Bitmap getSideBitmapForShipSize(@NonNull Resources resources, int size) {
        switch (size) {
            case 5:
                return Bitmaps.getBitmap(resources, R.drawable.aircraft_carrier);
            case 4:
                return Bitmaps.getBitmap(resources, R.drawable.battleship);
            case 3:
                return Bitmaps.getBitmap(resources, R.drawable.frigate);
            case 2:
            default:
                return Bitmaps.getBitmap(resources, R.drawable.gunboat);
        }
    }

}
