package com.ivygames.common.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class ExceptionEvent {
    @Nullable
    private static ExceptionEventImpl sImpl;

    static void setImpl(@NonNull ExceptionEventImpl impl) {
        sImpl = impl;
    }

    public static void send(@NonNull String action, @NonNull Exception e) {
        if (sImpl != null) {
            sImpl.send(action, e);
        }
    }

    public static void send(@NonNull String action, @NonNull String label) {
        if (sImpl != null) {
            sImpl.send(action, label);
        }
    }

    public static void send(@NonNull String action, @NonNull String label, @NonNull Exception e) {
        if (sImpl != null) {
            sImpl.send(action, label, e);
        }
    }

    public static void send(@NonNull String action) {
        if (sImpl != null) {
            sImpl.send(action);
        }
    }

}