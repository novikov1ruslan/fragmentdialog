package com.ivygames.common.analytics;

import android.support.annotation.NonNull;

public interface ExceptionReporter {
    void report(@NonNull String message);

    void report(@NonNull Exception e);
}
