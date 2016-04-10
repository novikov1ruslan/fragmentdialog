package com.ivygames.morskoiboi;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.screen.Screen;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;
import com.ivygames.morskoiboi.screen.help.HelpScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.ranks.RanksListScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;
import com.ivygames.morskoiboi.screen.settings.SettingsScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

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
        VibratorFacade vibratorFacade = new VibratorFacade(parent);
        return new SettingsScreen(parent, apiClient, settings, vibratorFacade);
    }

    public static SelectGameScreen newSelectGameScreen() {
        return new SelectGameScreen(parent);
    }

    public static BoardSetupScreen newBoardSetupScreen() {
        return new BoardSetupScreen(parent);
    }

    public static BluetoothScreen newBluetoothScreen() {
        return new BluetoothScreen(parent);
    }

    public static DeviceListScreen newDeviceListScreen() {
        return new DeviceListScreen(parent);
    }

    public static InternetGameScreen newInternetGameScreen() {
        return new InternetGameScreen(parent);
    }

    public static GameplayScreen newGameplayScreen() {
        return new GameplayScreen(parent);
    }

    public static WinScreen newWinScreen(@NonNull Bundle args) {
        return new WinScreen(args, parent);
    }

    public static LostScreen newLostScreen() {
        return new LostScreen(parent);
    }

    public static RanksListScreen newRanksListScreen() {
        return new RanksListScreen(parent);
    }
}