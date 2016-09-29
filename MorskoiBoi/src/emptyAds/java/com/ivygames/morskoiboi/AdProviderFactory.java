package com.ivygames.morskoiboi;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.ads.AdProvider;
import com.ivygames.common.ads.NoAdsAdProvider;

public class AdProviderFactory {
    static AdProvider create(@NonNull Activity activity, AndroidDevice mDevice) {
        return new NoAdsAdProvider();
    }
}
