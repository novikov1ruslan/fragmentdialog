package com.ivygames.morskoiboi.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;

import org.commons.logger.Ln;

import java.util.Map;

public final class UiEvent {
    private final EventBuilder builder;
    private static final String GA_CAT_UI = "ui";

    public UiEvent(String action) {
        Ln.v("action=" + action);
        builder = new EventBuilder(UiEvent.GA_CAT_UI, action);
    }

    public UiEvent(String action, String label) {
        Ln.v("action=" + action + "; label=" + label);
        builder = new EventBuilder(UiEvent.GA_CAT_UI, action).setLabel(label);
    }

    public UiEvent(String action, int value) {
        Ln.v("action=" + action + "; value=" + value);
        builder = new EventBuilder(UiEvent.GA_CAT_UI, action).setValue(value);
    }

    public UiEvent(String action, String label, int value) {
        Ln.v("action=" + action + "; label=" + label + "; value=" + value);
        builder = new EventBuilder(UiEvent.GA_CAT_UI, action).setLabel(label).setValue(value);
    }

    public static void screenView(String screenName) {
        GlobalTracker.sTracker.setScreenName(screenName);
        GlobalTracker.sTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public Map<String, String> build() {
        return builder.build();
    }

    public static void send(String action) {
        GlobalTracker.sTracker.send(new UiEvent(action).build());
    }

    public static void send(String action, String label) {
        GlobalTracker.sTracker.send(new UiEvent(action, label).build());
    }

    public static void send(String action, int value) {
        GlobalTracker.sTracker.send(new UiEvent(action, value).build());
    }
}