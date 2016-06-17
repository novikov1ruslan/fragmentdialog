package com.ivygames.morskoiboi;

import android.app.Activity;

public class AdProviderFactory {
    private static AdProvider sAdProvider = new NoAdsAdProvider();

    static void init(Activity activity) {

    }

    static void noAds() {

    }

    public static AdProvider getAdProvider() {
        return sAdProvider;
    }
}
