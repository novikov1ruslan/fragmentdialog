package com.ivygames.morskoiboi.rt;

import com.google.android.gms.games.GamesStatusCodes;

public class RtUtils {

    public static String name(int status) {

        switch (status) {
            // data was successfully loaded and is up-to-date
            case GamesStatusCodes.STATUS_OK:
                return "STATUS_OK";
            // the client needs to reconnect to the service to access this data
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                return "STATUS_CLIENT_RECONNECT_REQUIRED";
            // the client failed to connect to the network
            case GamesStatusCodes.STATUS_REAL_TIME_CONNECTION_FAILED:
                return "STATUS_REAL_TIME_CONNECTION_FAILED";

            // the game does not support multiplayer
            case GamesStatusCodes.STATUS_MULTIPLAYER_DISABLED:
                return "STATUS_MULTIPLAYER_DISABLED";
            // an unexpected error occurred in the service
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                return "STATUS_INTERNAL_ERROR";

            case GamesStatusCodes.STATUS_REAL_TIME_INACTIVE_ROOM:
                return "STATUS_REAL_TIME_INACTIVE_ROOM";

            // A network error occurred while attempting to perform an operation that requires network access.
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                return "STATUS_NETWORK_ERROR_OPERATION_FAILED";

            default:
                return "UNKNOWN(" + status + ")";
        }
    }
}
