package com.ivygames.morskoiboi.variant;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public interface FleetBitmaps {
    Bitmap getBitmapForShipSize(@NonNull Resources resources, int size);
}
