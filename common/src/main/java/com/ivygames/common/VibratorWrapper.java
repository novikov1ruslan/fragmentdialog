package com.ivygames.common;

import android.os.Vibrator;
import android.support.annotation.NonNull;

public class VibratorWrapper {

    @NonNull
    private final Vibrator mVibrator;

    public VibratorWrapper(@NonNull Vibrator vibrator) {
        mVibrator = vibrator;
    }

    public void vibrate(int milliseconds) {
        if (hasVibrator()) {
            mVibrator.vibrate(milliseconds);
        }
    }

    public boolean hasVibrator() {
        return mVibrator.hasVibrator();
    }
}
