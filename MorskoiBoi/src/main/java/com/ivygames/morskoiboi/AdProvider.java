package com.ivygames.morskoiboi;

import android.app.Activity;

import com.google.android.gms.plus.model.people.Person;

public interface AdProvider {
    void needToShowInterstitialAfterPlay();

    void showInterstitialAfterPlay();

    void setPerson(Person person);

    void resume(Activity activity);

    void pause();

    void destroy();
}
