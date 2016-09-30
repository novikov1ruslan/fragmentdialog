package com.ivygames.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

public class AndroidUtils {
    private AndroidUtils() {}


    public static void printIntent(@NonNull Intent intent) {
        Ln.v("intent=" + intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            printExtras(extras);
        }
    }

    private static void printExtras(@NonNull Bundle extras) {
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            if (value == null) {
                Ln.v(key + "= null");
            } else {
                Ln.v(key + "=" + value.toString() + "[" + value.getClass().getName() + "]");
            }
        }
    }
}
