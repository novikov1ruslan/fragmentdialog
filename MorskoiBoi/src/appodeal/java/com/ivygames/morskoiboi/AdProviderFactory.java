package com.ivygames.morskoiboi;

import android.app.Activity;

public class AdProviderFactory {
    private static AdProvider sAdProvider = new NoAdsAdProvider();

    static void init(Activity activity) {
        sAdProvider = new AppodealAdProvider(activity);
    }

    static void noAds() {
        sAdProvider = new NoAdsAdProvider();
    }

    public static AdProvider getAdProvider() {
        return sAdProvider;
    }
}
