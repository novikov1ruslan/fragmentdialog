package com.ivygames.morskoiboi;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public interface FleetBitmaps {
    Bitmap getSideBitmapForShipSize(@NonNull Resources resources, int size);
    Bitmap getTopBitmapForShipSize(@NonNull Resources resources, int size);
}
