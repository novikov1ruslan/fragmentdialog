package com.ivygames.common.ads;

public interface AdProvider {
    void needToShowAfterPlayAd();

    void showAfterPlayAd();

    void resume();

    void pause();

    void destroy();
}
