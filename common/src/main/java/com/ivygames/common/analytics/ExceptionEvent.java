package com.ivygames.common.analytics;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;

import java.util.Map;

public final class ExceptionEvent {
    private final EventBuilder builder;
    private static final String GA_CAT_EXCEPTION = "exception";

    public static void send(String action, Exception e) {
        GlobalTracker.sTracker.send(new ExceptionEvent(action, e).build());
    }

    public static void send(String action, String label) {
        GlobalTracker.sTracker.send(new ExceptionEvent(action, label).build());
    }

    public static void send(String action, String label, Exception e) {
        GlobalTracker.sTracker.send(new ExceptionEvent(action, label + e.getClass() + "; " + e.getMessage()).build());
    }

    public static void send(String action) {
        GlobalTracker.sTracker.send(new ExceptionEvent(action).build());
    }

    public ExceptionEvent(String action, Exception e) {
        builder = new EventBuilder(GA_CAT_EXCEPTION, action).setLabel("" + e);
    }

    public ExceptionEvent(String action) {
        builder = new EventBuilder(GA_CAT_EXCEPTION, action);
    }

    public ExceptionEvent(String action, String label) {
        builder = new EventBuilder(GA_CAT_EXCEPTION, action).setLabel(label);
    }

    public ExceptionEvent(String action, String label, int value) {
        builder = new EventBuilder(GA_CAT_EXCEPTION, action).setLabel(label).setValue(value);
    }

    public ExceptionEvent(String action, int value) {
        builder = new EventBuilder(GA_CAT_EXCEPTION, action).setValue(value);
    }

    public Map<String, String> build() {
        return builder.build();
    }

}