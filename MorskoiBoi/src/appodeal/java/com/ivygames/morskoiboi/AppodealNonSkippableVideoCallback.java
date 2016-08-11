package com.ivygames.morskoiboi;

import com.appodeal.ads.NonSkippableVideoCallbacks;

import org.commons.logger.Ln;

class AppodealNonSkippableVideoCallback implements NonSkippableVideoCallbacks {
    @Override
    public void onNonSkippableVideoLoaded() {
        Ln.v("loaded");
    }

    @Override
    public void onNonSkippableVideoFailedToLoad() {
        Ln.v("failed to load");
    }

    @Override
    public void onNonSkippableVideoShown() {
        Ln.v("shown");
    }

    @Override
    public void onNonSkippableVideoFinished() {
        Ln.v("finished");
    }

    @Override
    public void onNonSkippableVideoClosed(boolean b) {
        Ln.v("closed");
    }
}
