package com.ivygames.common.analytics;

import org.acra.ACRA;

public class ExceptionHandler {

    private ExceptionHandler() {
        // utility
    }

    public static void reportException(String message) {
        reportException(new Acra(message));
    }

    public static void reportException(Exception e) {
        ACRA.getErrorReporter().handleException(e);
    }
}
