package org.commons.logger;

import android.support.annotation.NonNull;

public class LoggerUtils {

    @NonNull
    public static String getSimpleName(@NonNull Object o) {
        return o.getClass().getSimpleName() + "#" + (o.hashCode() % 1000);
    }
}
