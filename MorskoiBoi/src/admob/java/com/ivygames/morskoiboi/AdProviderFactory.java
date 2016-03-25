package com.ivygames.morskoiboi;

import android.app.Activity;

public class AdProviderFactory {
    private static AdProvider sAdProvider = new NoAdsAdProvider();

    static void init(Activity activity) {
        sAdProvider = new AdmobAdProvider(activity);
    }

    static void setAdProvider(AdProvider adProvider) {
        sAdProvider = adProvider;
    }

    public static AdProvider getAdProvider() {
        return sAdProvider;
    }
}
