package com.ivygames.common.ads;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

public class NoAdsAdProvider implements AdProvider {
    @Override
    public void needToShowAfterPlayAd() {
        Ln.v("no ads");
    }

    @Override
    public void showAfterPlayAd() {
        Ln.v("no ads");
    }

    @Override
    public void resume() {
        Ln.v("no ads");
    }

    @Override
    public void pause() {
        Ln.v("no ads");
    }

    @Override
    public void destroy() {
        Ln.v("no ads");
    }
}
