package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.screen.Screen;
import com.ivygames.morskoiboi.screen.help.HelpScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.settings.SettingsScreen;

public class GameHandler {

    private static BattleshipActivity parent;
    private static GoogleApiClientWrapper apiClient;
    private static GameSettings settings;

    public static void setParent(@NonNull BattleshipActivity activity) {
        parent = activity;
    }

    public static void setApiClient(@NonNull GoogleApiClientWrapper apiClient) {
        GameHandler.apiClient = apiClient;
    }

    public static void setSettings(@NonNull GameSettings settings) {
        GameHandler.settings = settings;
    }

    public static void setScreen(Screen screen) {

    }

    public static MainScreen newMainScreen() {
        return new MainScreen(parent, apiClient);
    }

    public static HelpScreen newHelpScreen() {
        return new HelpScreen(parent);
    }

    public static SettingsScreen newSettingsScreen() {
        return new SettingsScreen(parent, apiClient, settings);
    }
}
