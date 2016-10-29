package com.ivygames.common.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class ExceptionEvent {
    @Nullable
    private static ExceptionEventImpl sImpl;

    static void setImpl(@NonNull ExceptionEventImpl impl) {
        sImpl = impl;
    }

    public static void send(String action, Exception e) {
        if (sImpl != null) {
            sImpl.send(action, e);
        }
    }

    public static void send(String action, String label) {
        if (sImpl != null) {
            sImpl.send(action, label);
        }
    }

    public static void send(String action, String label, Exception e) {
        if (sImpl != null) {
            sImpl.send(action, label, e);
        }
    }

    public static void send(String action) {
        if (sImpl != null) {
            sImpl.send(action);
        }
    }

}