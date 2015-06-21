package com.ivygames.morskoiboi.analytics;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

public final class ExceptionEvent {
	private final EventBuilder builder;
	private static final String GA_CAT_EXCEPTION = "exception";

	public static void send(Tracker tracker, String action, Exception e) {
		tracker.send(new ExceptionEvent(action, e).build());
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