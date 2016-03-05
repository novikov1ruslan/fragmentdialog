package com.ivygames.morskoiboi;

public class AdProviderFactory {
    private static AdProvider sAdProvider;

    static void setAdProvider(AdProvider adProvider) {
        sAdProvider = adProvider;
    }

    public static AdProvider getAdProvider() {
        return sAdProvider;
    }
}
