package com.ivygames.morskoiboi;

public class Dependencies {

    private static GoogleApiClientWrapper sApiClient;

    private static InvitationManager sInvitationManager;

    public static void injectApiClient(GoogleApiClientWrapper apiClient) {
        sApiClient = apiClient;
    }

    public static GoogleApiClientWrapper getApiClient() {
        return sApiClient;
    }


    public static void injectInvitationManager(InvitationManager invitationManager) {
        sInvitationManager = invitationManager;
    }

    public static InvitationManager getsInvitationManager() {
        return sInvitationManager;
    }
}
