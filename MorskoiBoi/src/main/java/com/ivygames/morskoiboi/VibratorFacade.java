package com.ivygames.morskoiboi;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;

public class VibratorFacade {

    private static final int MIN_VERSION_SUPPORTING_HAS_VIBRATOR = 11;

    @NonNull
    private final Vibrator mVibrator;

    public static Vibrator getVibratorService(@NonNull Context context) {
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public VibratorFacade(@NonNull Context context) {
        mVibrator = getVibratorService(context);
    }

    public void vibrate(int milliseconds) {
        if (hasVibrator()) {
            mVibrator.vibrate(milliseconds);
        }
    }

    public boolean hasVibrator() {
        if (Build.VERSION.SDK_INT >= MIN_VERSION_SUPPORTING_HAS_VIBRATOR) {
            return mVibrator.hasVibrator();
        }
        return true;
    }
}
