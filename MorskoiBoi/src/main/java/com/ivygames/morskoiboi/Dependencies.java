package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ai.AiPlayerFactory;
import com.ivygames.common.AndroidDevice;
import com.ivygames.common.ads.AdProvider;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

import org.commons.logger.Ln;

import java.util.Random;

public class Dependencies {

    private static ApiClient sApiClient;
    private static RealTimeMultiplayer sMultiplayer;
    private static AchievementsManager sAchievementsManager;
    private static ProgressManager sProgressManager;
    private static AndroidDevice sAndroidDevice;
    private static GameSettings sGameSettings;
    private static Rules sRules;
    private static AdProvider sAdProvider;
    private static PlayerFactory sPlayerFactory;
    private static AiPlayerFactory sAiPlayerFactory;
    private static Placement sPlacement;
    private static Random sRandom;
    private static ScoresCalculator sScoresCalculator;

    public static void inject(@NonNull ApiClient apiClient) {
        sApiClient = apiClient;
        Ln.i(sApiClient);
    }

    public static ApiClient getApiClient() {
        return sApiClient;
    }


    public static void inject(@NonNull RealTimeMultiplayer multiplayer) {
        sMultiplayer = multiplayer;
        Ln.i(sMultiplayer);
    }

    public static RealTimeMultiplayer getMultiplayer() {
        return sMultiplayer;
    }

    public static void inject(@NonNull AchievementsManager achievementsManager) {
        sAchievementsManager = achievementsManager;
        Ln.i(sAchievementsManager);
    }

    public static AchievementsManager getAchievementsManager() {
        return sAchievementsManager;
    }

    public static void inject(@NonNull ProgressManager progressManager) {
        sProgressManager = progressManager;
        Ln.i(sProgressManager);
    }

    public static ProgressManager getProgressManager() {
        return sProgressManager;
    }

    public static void inject(@NonNull AndroidDevice androidDevice) {
        sAndroidDevice = androidDevice;
        Ln.i(sAndroidDevice);
    }

    public static AndroidDevice getDevice() {
        return sAndroidDevice;
    }

    public static void inject(@NonNull GameSettings settings) {
        sGameSettings = settings;
        Ln.i(sGameSettings);
    }

    public static GameSettings getSettings() {
        return sGameSettings;
    }

    public static void inject(@NonNull Rules rules) {
        sRules = rules;
        Ln.i(sRules);
    }

    public static Rules getRules() {
        return sRules;
    }

    public static void inject(@NonNull AdProvider adProvider) {
        sAdProvider = adProvider;
        Ln.i(sAdProvider);
    }

    public static AdProvider getAdProvider() {
        return sAdProvider;
    }

    public static void inject(@NonNull PlayerFactory playerFactory) {
        sPlayerFactory = playerFactory;
        Ln.i(sPlayerFactory);
    }

    public static PlayerFactory getPlayerFactory() {
        return sPlayerFactory;
    }

    public static void inject(@NonNull AiPlayerFactory playerFactory) {
        sAiPlayerFactory = playerFactory;
        Ln.i(sAiPlayerFactory);
    }

    public static AiPlayerFactory getAiPlayerFactory() {
        return sAiPlayerFactory;
    }

    public static void inject(@NonNull Placement placement) {
        sPlacement = placement;
        Ln.i(sPlacement);
    }

    @NonNull
    public static Placement getPlacement() {
        return sPlacement;
    }

    public static void inject(@NonNull Random random) {
        sRandom = random;
        Ln.i(sRandom);
    }

    @NonNull
    public static Random getRandom() {
        return sRandom;
    }

    public static void inject(@NonNull ScoresCalculator scoresCalculator) {
        sScoresCalculator = scoresCalculator;
        Ln.i(sScoresCalculator);
    }

    @NonNull
    public static ScoresCalculator getScoresCalculator() {
        return sScoresCalculator;
    }
}
