package com.ivygames.morskoiboi.analytics;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;

import java.util.Map;

public final class AnalyticsEvent {
    private final EventBuilder builder;
    private static final String GA_CAT_GAME = "game";

    public AnalyticsEvent(String action) {
        builder = new EventBuilder(AnalyticsEvent.GA_CAT_GAME, action);
    }

    public AnalyticsEvent(String action, String label) {
        builder = new EventBuilder(AnalyticsEvent.GA_CAT_GAME, action).setLabel(label);
    }

    public AnalyticsEvent(String action, String label, int value) {
        builder = new EventBuilder(AnalyticsEvent.GA_CAT_GAME, action).setLabel(label).setValue(value);
    }

    public AnalyticsEvent(String action, int value) {
        builder = new EventBuilder(AnalyticsEvent.GA_CAT_GAME, action).setValue(value);
    }

    public Map<String, String> build() {
        return builder.build();
    }
}