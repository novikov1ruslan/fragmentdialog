package com.ivygames.morskoiboi.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;

import org.commons.logger.Ln;

public final class UiEventImpl implements UiEventInterface {
    private static final String GA_CAT_UI = "ui";

    @Override
    public void screenView(String screenName) {
        GlobalTracker.sTracker.setScreenName(screenName);
        GlobalTracker.sTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void send(String action) {
        Ln.v("action=" + action);
        EventBuilder builder = new EventBuilder(UiEventImpl.GA_CAT_UI, action);
        GlobalTracker.sTracker.send(builder.build());
    }

    @Override
    public void send(String action, String label) {
        Ln.v("action=" + action + "; label=" + label);
        EventBuilder builder = new EventBuilder(UiEventImpl.GA_CAT_UI, action).setLabel(label);
        GlobalTracker.sTracker.send(builder.build());
    }

    @Override
    public void send(String action, int value) {
        Ln.v("action=" + action + "; value=" + value);
        EventBuilder builder = new EventBuilder(UiEventImpl.GA_CAT_UI, action).setValue(value);
        GlobalTracker.sTracker.send(builder.build());
    }
}