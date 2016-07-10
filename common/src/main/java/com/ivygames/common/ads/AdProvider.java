package com.ivygames.common.ads;

import android.app.Activity;

public interface AdProvider {
    void needToShowInterstitialAfterPlay();

    void showInterstitialAfterPlay();

    void resume(Activity activity);

    void pause();

    void destroy();
}
