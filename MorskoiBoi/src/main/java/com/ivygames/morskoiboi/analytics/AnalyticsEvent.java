package com.ivygames.morskoiboi.analytics;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Rank;

import org.commons.logger.Ln;

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

    public static void trackPromotionEvent(int oldProgress, int newProgress, Tracker tracker) {
        Rank lastRank = Rank.getBestRankForScore(oldProgress);
        Rank newRank = Rank.getBestRankForScore(newProgress);
        if (newRank != lastRank) {
            GameSettings.get().newRankAchieved(true);
            String label = lastRank + " promoted to " + newRank;
            tracker.send(new AnalyticsEvent("promotion", label, 1).build());
            Ln.i(label);
        }
    }

    public Map<String, String> build() {
        return builder.build();
    }

    public static void send(Tracker tracker, String action) {
        tracker.send(new AnalyticsEvent(action).build());
    }

    public static void send(Tracker tracker, String action, String label) {
        tracker.send(new AnalyticsEvent(action, label).build());
    }
}