package com.ivygames.common.analytics;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ivygames.common.AndroidDevice;
import com.ivygames.common.BuildConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class GlobalTracker {

    private GlobalTracker(){}

    public static void initAnalytics(@NonNull Application application, @NonNull String analyticsKey) {
        // Get the GoogleAnalytics singleton. Note that the SDK uses
        // the application context to avoid leaking the current context.
        // Logger interface is deprecated. Use adb shell setprop log.tag.GAv4 DEBUG to enable debug logging for Google Analytics.
        GoogleAnalytics.getInstance(application).setDryRun(BuildConfig.DEBUG);
        Tracker tracker = GoogleAnalytics.getInstance(application).newTracker(analyticsKey);
        tracker.enableAdvertisingIdCollection(true);
        UiEvent.set(new UiEventImpl(tracker));
        AnalyticsEvent.setImpl(new AnalyticsEventImpl(tracker));
        WarningEvent.setImpl(new WarningEventImpl(tracker));
        ExceptionEvent.setImpl(new ExceptionEventImpl(tracker));

        Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter exceptionReporter = new ExceptionReporter(tracker, exceptionHandler, application);
        // TODO: Make exceptionReporter the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(exceptionReporter);
        exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
    }

    private static class AnalyticsExceptionParser implements ExceptionParser {

        @Override
        public String getDescription(String threadName, Throwable throwable) {
            return "[" + threadName + "] " + getStackTrace(throwable) + " (" + AndroidDevice.getDeviceInfo() + ")";
        }

        private String getStackTrace(Throwable throwable) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            throwable.printStackTrace(printWriter);

            return result.toString();
        }
    }

}
