package com.ivygames.common.analytics;

import android.support.annotation.NonNull;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;

class ExceptionEventImpl {

    private static final String GA_CAT_EXCEPTION = "exception";

    @NonNull
    private final Tracker mTracker;

    ExceptionEventImpl(@NonNull Tracker tracker) {
        mTracker = tracker;
    }

    public void send(String action, Exception e) {
        EventBuilder builder = new EventBuilder(GA_CAT_EXCEPTION, action).setLabel("" + e);
        mTracker.send(builder.build());
    }

    public void send(String action, String label) {
        EventBuilder builder = new EventBuilder(GA_CAT_EXCEPTION, action).setLabel(label);
        mTracker.send(builder.build());
    }

    public void send(String action, String label, Exception e) {
        EventBuilder builder = new EventBuilder(GA_CAT_EXCEPTION, action).setLabel(label + e.getClass() + "; " + e.getMessage());
        mTracker.send(builder.build());
    }

    public void send(String action) {
        EventBuilder builder = new EventBuilder(GA_CAT_EXCEPTION, action);
        mTracker.send(builder.build());
    }

}
