package com.ivygames.morskoiboi;

import android.app.Activity;

import com.ivygames.common.ads.AdProvider;
import com.ivygames.common.ads.NoAdsAdProvider;

public class AdProviderFactory {
    static AdProvider create(Activity activity) {
        return new NoAdsAdProvider();
    }
}
