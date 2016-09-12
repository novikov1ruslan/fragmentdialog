package com.ivygames.common.analytics;

import android.support.annotation.NonNull;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;

final class AnalyticsEventImpl {
    private static final String GA_CAT_GAME = "game";

    @NonNull
    private final Tracker mTracker;

    public AnalyticsEventImpl(@NonNull Tracker tracker) {
        mTracker = tracker;
    }

    public void send(String action) {
        EventBuilder builder = new EventBuilder(AnalyticsEventImpl.GA_CAT_GAME, action);
        mTracker.send(builder.build());
    }

    public void send(String action, String label) {
        EventBuilder builder = new EventBuilder(AnalyticsEventImpl.GA_CAT_GAME, action).setLabel(label);
        mTracker.send(builder.build());
    }

    public void send(String action, String label, int value) {
        EventBuilder builder = new EventBuilder(AnalyticsEventImpl.GA_CAT_GAME, action).setLabel(label).setValue(value);
        mTracker.send(builder.build());
    }

    public void send(String action, int value) {
        EventBuilder builder = new EventBuilder(AnalyticsEventImpl.GA_CAT_GAME, action).setValue(value);
        mTracker.send(builder.build());
    }
}
