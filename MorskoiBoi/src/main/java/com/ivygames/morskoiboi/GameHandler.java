package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.Screen;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;
import com.ivygames.morskoiboi.screen.gameplay.AsyncTurnTimerFactory;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;
import com.ivygames.morskoiboi.screen.gameplay.TurnTimerController;
import com.ivygames.morskoiboi.screen.help.HelpScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.ranks.RanksListScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;
import com.ivygames.morskoiboi.screen.settings.SettingsScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import java.util.Collection;

public class GameHandler {

    private static BattleshipActivity activity;
    private static GoogleApiClientWrapper apiClient;
    private static GameSettings settings;

    private GameHandler() {
    }

    public static void setActivity(@NonNull BattleshipActivity activity) {
        GameHandler.activity = activity;
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
        return new MainScreen(activity, apiClient, settings);
    }

    public static HelpScreen newHelpScreen() {
        return new HelpScreen(activity);
    }

    public static SettingsScreen newSettingsScreen() {
        VibratorFacade vibratorFacade = new VibratorFacade(activity);
        return new SettingsScreen(activity, apiClient, settings, vibratorFacade);
    }

    public static SelectGameScreen newSelectGameScreen() {
        return new SelectGameScreen(activity, settings);
    }

    public static BoardSetupScreen newBoardSetupScreen(@NonNull Game game) {
        return new BoardSetupScreen(activity, game);
    }

    public static BluetoothScreen newBluetoothScreen(@NonNull BluetoothAdapterWrapper adapter) {
        return new BluetoothScreen(activity, adapter);
    }

    public static DeviceListScreen newDeviceListScreen(@NonNull BluetoothAdapterWrapper adapter) {
        return new DeviceListScreen(activity, adapter);
    }

    public static InternetGameScreen newInternetGameScreen() {
        return new InternetGameScreen(activity);
    }

    public static GameplayScreen newGameplayScreen(@NonNull Game game) {
        return new GameplayScreen(activity, game,
                new TurnTimerController(game.getTurnTimeout(),
                        new AsyncTurnTimerFactory()));
    }

    public static WinScreen newWinScreen(@NonNull Game game,
                                         @NonNull Collection<Ship> fleet,
                                         @NonNull ScoreStatistics statistics,
                                         boolean opponentSurrendered) {
        return new WinScreen(activity, game, fleet, statistics, opponentSurrendered);
    }

    public static LostScreen newLostScreen(@NonNull Game game) {
        return new LostScreen(activity, game);
    }

    public static RanksListScreen newRanksListScreen() {
        return new RanksListScreen(activity, settings);
    }
}
