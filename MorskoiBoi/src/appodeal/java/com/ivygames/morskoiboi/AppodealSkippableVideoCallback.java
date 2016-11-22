package com.ivygames.morskoiboi;

import com.appodeal.ads.SkippableVideoCallbacks;

import org.commons.logger.Ln;

class AppodealSkippableVideoCallback implements SkippableVideoCallbacks {

    @Override
    public void onSkippableVideoLoaded() {
        Ln.v("loaded");
    }

    @Override
    public void onSkippableVideoFailedToLoad() {
        Ln.v("failed to load");
    }

    @Override
    public void onSkippableVideoShown() {
        Ln.v("shown");
    }

    @Override
    public void onSkippableVideoFinished() {
        Ln.v("finished");
    }

    @Override
    public void onSkippableVideoClosed(boolean b) {
        Ln.v("closed: " + b);
    }
}
