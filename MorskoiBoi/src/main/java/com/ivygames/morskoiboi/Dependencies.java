package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.invitations.InvitationManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

public class Dependencies {

    private static GoogleApiClientWrapper sApiClient;
    private static InvitationManager sInvitationManager;
    private static AchievementsManager sAchievementsManager;
    private static ProgressManager sProgressManager;
    private static AndroidDevice sAndroidDevice;
    private static GameSettings sGameSettings;
    private static Rules sRules;

    static void inject(@NonNull GoogleApiClientWrapper apiClient) {
        sApiClient = apiClient;
    }

    public static GoogleApiClientWrapper getApiClient() {
        return sApiClient;
    }


    static void inject(@NonNull InvitationManager invitationManager) {
        sInvitationManager = invitationManager;
    }

    public static InvitationManager getInvitationManager() {
        return sInvitationManager;
    }

    static void inject(@NonNull AchievementsManager achievementsManager) {
        sAchievementsManager = achievementsManager;
    }

    public static AchievementsManager getAchievementsManager() {
        return sAchievementsManager;
    }

    static void inject(@NonNull ProgressManager progressManager) {
        sProgressManager = progressManager;
    }

    public static ProgressManager getProgressManager() {
        return sProgressManager;
    }

    public static void inject(@NonNull AndroidDevice androidDevice) {
        sAndroidDevice = androidDevice;
    }

    public static AndroidDevice getDevice() {
        return sAndroidDevice;
    }

    public static void inject(@NonNull GameSettings settings) {
        sGameSettings = settings;
    }

    public static GameSettings getSettings() {
        return sGameSettings;
    }

    public static void inject(@NonNull Rules rules) {
        sRules = rules;
    }

    public static Rules getRules() {
        return sRules;
    }
}
