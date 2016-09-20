package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.ads.AdProvider;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.multiplayer.MultiplayerManager;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

import org.commons.logger.Ln;

public class Dependencies {

    private static ApiClient sApiClient;
    private static MultiplayerManager sMultiplayer;
    private static AchievementsManager sAchievementsManager;
    private static ProgressManager sProgressManager;
    private static AndroidDevice sAndroidDevice;
    private static GameSettings sGameSettings;
    private static Rules sRules;
    private static AdProvider sAdProvider;

    static void inject(@NonNull ApiClient apiClient) {
        sApiClient = apiClient;
        Ln.d(sApiClient);
    }

    public static ApiClient getApiClient() {
        return sApiClient;
    }


    static void inject(@NonNull MultiplayerManager invitationManager) {
        sMultiplayer = invitationManager;
        Ln.d(sMultiplayer);
    }

    public static MultiplayerManager getMultiplayer() {
        return sMultiplayer;
    }

    static void inject(@NonNull AchievementsManager achievementsManager) {
        sAchievementsManager = achievementsManager;
        Ln.d(sAchievementsManager);
    }

    public static AchievementsManager getAchievementsManager() {
        return sAchievementsManager;
    }

    static void inject(@NonNull ProgressManager progressManager) {
        sProgressManager = progressManager;
        Ln.d(sProgressManager);
    }

    public static ProgressManager getProgressManager() {
        return sProgressManager;
    }

    public static void inject(@NonNull AndroidDevice androidDevice) {
        sAndroidDevice = androidDevice;
        Ln.d(sAndroidDevice);
    }

    public static AndroidDevice getDevice() {
        return sAndroidDevice;
    }

    public static void inject(@NonNull GameSettings settings) {
        sGameSettings = settings;
        Ln.d(sGameSettings);
    }

    public static GameSettings getSettings() {
        return sGameSettings;
    }

    public static void inject(@NonNull Rules rules) {
        sRules = rules;
        Ln.d(sRules);
    }

    public static Rules getRules() {
        return sRules;
    }

    public static void inject(@NonNull AdProvider adProvider) {
        sAdProvider = adProvider;
        Ln.d(sAdProvider);
    }

    public static AdProvider getAdProvider() {
        return sAdProvider;
    }
}
