package com.ivygames.common.analytics;

import org.acra.ACRA;
import org.commons.logger.Ln;

public class ExceptionHandler {

    private ExceptionHandler() {
        // utility
    }

    public static void reportException(String message) {
        reportException(new Acra(message));
        Ln.e(message);
    }

    public static void reportException(Exception e) {
        ACRA.getErrorReporter().handleException(e);
    }
}
