package com.ivygames.morskoiboi;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.ads.AdProvider;

public class AdProviderFactory {
    static AdProvider create(@NonNull Activity activity, @NonNull AndroidDevice device) {
        return new AppodealAdProvider(activity, device);
    }
}
