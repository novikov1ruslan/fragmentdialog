package com.ivygames.morskoiboi;

import com.appodeal.ads.InterstitialCallbacks;

import org.commons.logger.Ln;

class AppodealInterstitialCallback implements InterstitialCallbacks {
    @Override
    public void onInterstitialLoaded(boolean loaded) {
        Ln.v("loaded");
    }

    @Override
    public void onInterstitialFailedToLoad() {
        Ln.v("failed to load");
    }

    @Override
    public void onInterstitialShown() {
        Ln.v("shown");
    }

    @Override
    public void onInterstitialClicked() {
        Ln.v("clicked");
    }

    @Override
    public void onInterstitialClosed() {
        Ln.v("closed");
    }

}
