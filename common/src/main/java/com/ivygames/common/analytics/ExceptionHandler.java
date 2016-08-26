package com.ivygames.common.analytics;

import org.acra.ACRA;
import org.commons.logger.Ln;

public class ExceptionHandler {

    private static boolean sDryRun;

    private ExceptionHandler() {
        // utility
    }

    public static void setDryRun(boolean dryRun) {
        sDryRun = dryRun;
    }

    public static void reportException(String message) {
        Ln.e(message);
        if (sDryRun) {
            return;
        }
        reportException(new Acra(message));
    }

    public static void reportException(Exception e) {
        if (sDryRun) {
            return;
        }
        ACRA.getErrorReporter().handleException(e);
    }
}
