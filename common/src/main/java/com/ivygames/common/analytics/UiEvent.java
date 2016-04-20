package com.ivygames.common.analytics;

import android.support.annotation.NonNull;

public final class UiEvent {
    public static final String GA_ACTION_BACK = "back";
    public static final String GA_ACTION_SIGN_IN = "sign_in";

    private static @NonNull UiEventInterface sImpl = new UiEventDummy();

    public static void set(UiEventInterface uiEvent) {
        sImpl = uiEvent;
    }

    public static void screenView(String screenName) {
        sImpl.screenView(screenName);
    }

    public static void send(String action) {
        sImpl.send(action);
    }

    public static void send(String action, String label) {
        sImpl.send(action, label);
    }

    public static void send(String action, int value) {
        sImpl.send(action, value);
    }
}