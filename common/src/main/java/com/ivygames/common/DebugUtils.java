package com.ivygames.common;

import android.support.annotation.NonNull;

public class DebugUtils {

    @NonNull
    public static String getSimpleName(@NonNull Object o) {
        return o.getClass().getSimpleName() + "#" + (o.hashCode() % 1000);
    }
}
