package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.invitations.InvitationManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

public class Dependencies {

    private static GoogleApiClientWrapper sApiClient;

    private static InvitationManager sInvitationManager;
    private static AchievementsManager sAchievementsManager;
    private static ProgressManager sProgressManager;

    static void injectApiClient(GoogleApiClientWrapper apiClient) {
        sApiClient = apiClient;
    }

    public static GoogleApiClientWrapper getApiClient() {
        return sApiClient;
    }


    static void injectInvitationManager(InvitationManager invitationManager) {
        sInvitationManager = invitationManager;
    }

    public static InvitationManager getInvitationManager() {
        return sInvitationManager;
    }

    static void injectAchievementsManager(AchievementsManager achievementsManager) {
        sAchievementsManager = achievementsManager;
    }

    public static AchievementsManager getAchievementsManager() {
        return sAchievementsManager;
    }

    static void injectProgressManager(ProgressManager progressManager) {
        sProgressManager = progressManager;
    }

    public static ProgressManager getProgressManager() {
        return sProgressManager;
    }
}
