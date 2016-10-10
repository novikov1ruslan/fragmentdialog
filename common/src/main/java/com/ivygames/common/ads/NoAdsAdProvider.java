package com.ivygames.common.ads;

import org.commons.logger.Ln;

public class NoAdsAdProvider implements AdProvider {
    private static final boolean LOG_ENABLED = false;

    @Override
    public void needToShowAfterPlayAd() {
        if (LOG_ENABLED) {
            Ln.v("no ads");
        }
    }

    @Override
    public void showAfterPlayAd() {
        if (LOG_ENABLED) {
            Ln.v("no ads");
        }
    }

    @Override
    public void resume() {
        if (LOG_ENABLED) {
            Ln.v("no ads");
        }
    }

    @Override
    public void pause() {
        if (LOG_ENABLED) {
            Ln.v("no ads");
        }
    }

    @Override
    public void destroy() {
        if (LOG_ENABLED) {
            Ln.v("no ads");
        }
    }
}
