package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

public class AndroidDeviceFactory {
    private static AndroidDevice sAndroidDevice;

    public static void inject(@NonNull AndroidDevice androidDevice) {
        sAndroidDevice = androidDevice;
    }

    public static AndroidDevice getDevice() {
        return sAndroidDevice;
    }
}
