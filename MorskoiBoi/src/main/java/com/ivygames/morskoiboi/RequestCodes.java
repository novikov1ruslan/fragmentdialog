package com.ivygames.morskoiboi;

public class RequestCodes {
    public static final int RC_SELECT_PLAYERS = 10000;
    public static final int RC_INVITATION_INBOX = 10001;
    public final static int RC_WAITING_ROOM = 10002;
    public static final int RC_ENSURE_DISCOVERABLE = 3;
    // Request code used to invoke sign in user interactions.
    public static final int RC_SIGN_IN = 9001;
    public static final int SERVICE_RESOLVE = 9002;
    public static final int RC_UNUSED = 0;
    public static final int PLUS_ONE_REQUEST_CODE = 20001;
    public static final int RC_ENABLE_BT = 2;
    public static final int RC_PURCHASE = 10003;

    public static String getDebugName(int requestCode) {
        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                return "RC_SELECT_PLAYERS";
            case RC_INVITATION_INBOX:
                return "RC_INVITATION_INBOX";
            case RC_WAITING_ROOM:
                return "RC_WAITING_ROOM";
            case RC_ENSURE_DISCOVERABLE:
                return "RC_ENSURE_DISCOVERABLE";
            case RC_SIGN_IN:
                return "RC_SIGN_IN";
            case SERVICE_RESOLVE:
                return "SERVICE_RESOLVE";
            case RC_UNUSED:
                return "RC_UNUSED";
            case PLUS_ONE_REQUEST_CODE:
                return "PLUS_ONE_REQUEST_CODE";
            case RC_ENABLE_BT:
                return "RC_ENABLE_BT";
            case RC_PURCHASE:
                return "RC_PURCHASE";
            default:
                return "UNKNOWN[" + requestCode + "]";
        }
    }
}
