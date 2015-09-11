package com.ivygames.morskoiboi.analytics;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;

import java.util.Map;

public final class WarningEvent {
    private static final String GA_CAT_WARNING = "warning";
    private final EventBuilder builder;

    public WarningEvent(String action) {
        builder = new EventBuilder(WarningEvent.GA_CAT_WARNING, action);
    }

    public WarningEvent(String action, String label) {
        builder = new EventBuilder(WarningEvent.GA_CAT_WARNING, action).setLabel(label);
    }

    public WarningEvent(String action, String label, int value) {
        builder = new EventBuilder(WarningEvent.GA_CAT_WARNING, action).setLabel(label).setValue(value);
    }

    public WarningEvent(String action, int value) {
        builder = new EventBuilder(WarningEvent.GA_CAT_WARNING, action).setValue(value);
    }

    public Map<String, String> build() {
        return builder.build();
    }

    public static void send(String message) {
        GlobalTracker.sTracker.send(new WarningEvent(message).build());
    }
}