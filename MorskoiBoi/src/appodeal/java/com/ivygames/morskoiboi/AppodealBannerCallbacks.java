package com.ivygames.morskoiboi;

import com.appodeal.ads.BannerCallbacks;

import org.commons.logger.Ln;

class AppodealBannerCallbacks implements BannerCallbacks {
    @Override
    public void onBannerLoaded(int i, boolean b) {
        Ln.v("loaded");
    }

    @Override
    public void onBannerFailedToLoad() {
        Ln.v("failed to load");
    }

    @Override
    public void onBannerShown() {
        Ln.v("shown");
    }

    @Override
    public void onBannerClicked() {
        Ln.v("clicked");
    }
}
