package com.ivygames.morskoiboi;

import android.app.Activity;

import com.ivygames.common.ads.AdProvider;

public class AdProviderFactory {
    
    public static AdProvider create(Activity activity) {
        return new AdmobAdProvider(activity);
    }
}
