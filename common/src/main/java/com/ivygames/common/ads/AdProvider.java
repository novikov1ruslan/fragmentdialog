package com.ivygames.common.ads;

import android.app.Activity;

public interface AdProvider {
    void needToShowAfterPlayAd();

    void showAfterPlayAd();

    void resume(Activity activity);

    void pause();

    void destroy();
}
