package com.ivygames.morskoiboi;

import android.support.v4.app.FragmentTransaction;

public class Animations {

    private Animations() {
    }

    public static FragmentTransaction animateTransition(FragmentTransaction ft) {
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        return ft;
    }
}
