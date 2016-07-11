package com.ivygames.common;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;

public class VibratorWrapper {

    @NonNull
    private final Vibrator mVibrator;

    public VibratorWrapper(@NonNull Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
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
