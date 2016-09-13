package com.ivygames.common.analytics;

import android.support.annotation.NonNull;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;

class WarningEventImpl {
    private static final String GA_CAT_WARNING = "warning";

    @NonNull
    private final Tracker mTracker;

    public WarningEventImpl(@NonNull Tracker tracker) {
        mTracker = tracker;
    }

    public void send(@NonNull String action) {
        EventBuilder builder = new EventBuilder(GA_CAT_WARNING, action);
        mTracker.send(builder.build());
    }

    public void send(@NonNull String action, @NonNull String label) {
        EventBuilder builder = new EventBuilder(GA_CAT_WARNING, action).setLabel(label);
        mTracker.send(builder.build());
    }

    public void send(@NonNull String action, @NonNull String label, int value) {
        EventBuilder builder = new EventBuilder(GA_CAT_WARNING, action).setLabel(label).setValue(value);
        mTracker.send(builder.build());
    }

    public void send(@NonNull String action, int value) {
        EventBuilder builder = new EventBuilder(GA_CAT_WARNING, action).setValue(value);
        mTracker.send(builder.build());
    }
}
