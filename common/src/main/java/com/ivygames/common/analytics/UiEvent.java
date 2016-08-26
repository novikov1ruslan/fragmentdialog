package com.ivygames.common.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class UiEvent {
    public static final String GA_ACTION_BACK = "back";
    public static final String GA_ACTION_SIGN_IN = "sign_in";

    @Nullable
    private static UiEventImpl sImpl;

    public static void set(@NonNull UiEventImpl uiEvent) {
        sImpl = uiEvent;
    }

    public static void screenView(String screenName) {
        if (sImpl != null) {
            sImpl.screenView(screenName);
        }
    }

    public static void send(String action) {
        if (sImpl != null) {
            sImpl.send(action);
        }
    }

    public static void send(String action, String label) {
        if (sImpl != null) {
            sImpl.send(action, label);
        }
    }

    public static void send(String action, int value) {
        if (sImpl != null) {
            sImpl.send(action, value);
        }
    }
}