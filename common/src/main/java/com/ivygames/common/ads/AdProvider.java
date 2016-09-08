package com.ivygames.common.ads;

import android.app.Activity;
import android.support.annotation.NonNull;

public interface AdProvider {
    void needToShowAfterPlayAd();

    void showAfterPlayAd();

    void resume(@NonNull Activity activity);

    void pause();

    void destroy();
}
