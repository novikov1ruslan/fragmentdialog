package com.ivygames.morskoiboi;

public class AdProviderFactory {
    private static AdProvider sAdProvider = new NoAdsAdProvider();

    static void setAdProvider(AdProvider adProvider) {
        sAdProvider = adProvider;
    }

    static void init(Activity activity) {
        sAdProvider = new AppodealAdProvider(activity);
    }

    public static AdProvider getAdProvider() {
        return sAdProvider;
    }
}
