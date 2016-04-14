package com.ivygames.morskoiboi.analytics;

import org.acra.ACRA;

public class ExceptionHandler {

    private ExceptionHandler() {
        // utility
    }

    public static void reportException(String message) {
        ACRA.getErrorReporter().handleException(new Acra(message));
    }
}
