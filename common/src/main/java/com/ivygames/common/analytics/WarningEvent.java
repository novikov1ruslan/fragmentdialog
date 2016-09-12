package com.ivygames.common.analytics;

import android.support.annotation.Nullable;

public final class WarningEvent {

    @Nullable
    private static WarningEventImpl sImpl;

    public static void setImpl(WarningEventImpl impl) {
        sImpl = impl;
    }

    public static void send(String message) {
        if (sImpl != null) {
            sImpl.send(message);
        }
    }
}