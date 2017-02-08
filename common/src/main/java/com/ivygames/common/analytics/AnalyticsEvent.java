package com.ivygames.common.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class AnalyticsEvent {

    @Nullable
    private static AnalyticsEventImpl sImpl;

    static void setImpl(@NonNull AnalyticsEventImpl impl) {
        sImpl = impl;
    }

    public static void send(@NonNull String action) {
        if (sImpl != null) {
            sImpl.send(action);
        }
    }

    public static void send(@NonNull String action, @NonNull String label) {
        if (sImpl != null) {
            sImpl.send(action, label);
        }
    }

    public static void send(@NonNull String action, @NonNull String label, int value) {
        if (sImpl != null) {
            sImpl.send(action, label, value);
        }
    }
}
