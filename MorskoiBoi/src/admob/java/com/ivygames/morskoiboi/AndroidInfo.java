package com.ivygames.morskoiboi;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface AndroidInfo {
    boolean isConnectedToNetwork();

    boolean canResolveIntent(@NonNull Intent intent);

    boolean isTablet();

    String getVersionName();

    boolean isDebug();

    boolean isVibrationOn();

    boolean isGoogleServicesAvailable();

    boolean isBillingAvailable();
}
