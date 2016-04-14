package com.ivygames.common.analytics;

import android.support.annotation.NonNull;

public final class UiEvent {
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