package com.ivygames.common.analytics;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

public class ExceptionHandler {

    @NonNull
    private static ExceptionReporter sReporter = new ExceptionReporter() {
        @Override
        public void report(@NonNull String message) {
            Ln.i("Exception reporter not set: " + message);
        }

        @Override
        public void report(@NonNull Exception e) {
            Ln.i("Exception reporter not set: " + e);
        }
    };

    public static void reportException(@NonNull String message) {
        Ln.e(message);
        sReporter.report(message);
    }

    public static void reportException(Exception e) {
        Ln.e(e);
        sReporter.report(e);
    }

    public static void injectReporter(@NonNull ExceptionReporter reporter) {
        Ln.i("Exception reporter set to: " + reporter);
        sReporter = reporter;
    }
}
