package com.ivygames.morskoiboi;

import com.google.android.gms.common.api.GoogleApiClient;

public class GpgsUtils {
    private GpgsUtils() {
        // utils
    }

    public static String connectionCauseToString(int cause) {
        if (cause == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            return "CAUSE_NETWORK_LOST";
        } else if (cause == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            return "CAUSE_SERVICE_DISCONNECTED";
        }
        return "UNKNOWN(" + cause + ")";
    }

}
