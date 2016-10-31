package com.ivygames.morskoiboi.screen;

import android.os.Vibrator;
import android.support.annotation.NonNull;

import com.ivygames.common.VibratorWrapper;
import com.ivygames.common.timer.AsyncTurnTimerFactory;
import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ShipUtils;
import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;
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

import java.util.Collection;

public class ScreenCreator {
    private static final int ALLOWED_SKIPPED_TURNS = 2;

    private static BattleshipActivity activity;

    private ScreenCreator() {
    }

    public static void setActivity(@NonNull BattleshipActivity activity) {
        ScreenCreator.activity = activity;
    }

    @NonNull
    public static MainScreen newMainScreen() {
        return new MainScreen(activity);
    }

    @NonNull
    public static HelpScreen newHelpScreen() {
        return new HelpScreen(activity);
    }

    @NonNull
    public static SettingsScreen newSettingsScreen(Vibrator vibrator) {
        VibratorWrapper vibratorFacade = new VibratorWrapper(vibrator);
        return new SettingsScreen(activity, vibratorFacade);
    }

    @NonNull
    public static SelectGameScreen newSelectGameScreen() {
        return new SelectGameScreen(activity);
    }

    @NonNull
    public static BoardSetupScreen newBoardSetupScreen(@NonNull Game game, @NonNull Session session) {
        return new BoardSetupScreen(activity, game, session);
    }

    @NonNull
    public static BluetoothScreen newBluetoothScreen(@NonNull BluetoothAdapterWrapper adapter) {
        return new BluetoothScreen(activity, adapter);
    }

    @NonNull
    public static DeviceListScreen newDeviceListScreen(@NonNull BluetoothAdapterWrapper adapter) {
        return new DeviceListScreen(activity, adapter);
    }

    @NonNull
    public static InternetGameScreen newInternetGameScreen() {
        return new InternetGameScreen(activity);
    }

    @NonNull
    public static GameplayScreen newGameplayScreen(@NonNull Game game, @NonNull Session session) {
        return new GameplayScreen(activity, game, session,
                new TurnTimerController(game.getTurnTimeout(), ALLOWED_SKIPPED_TURNS,
                        new AsyncTurnTimerFactory()));
    }

    @NonNull
    public static WinScreen newWinScreen(@NonNull Game game,
                                         @NonNull Session session,
                                         @NonNull Collection<Ship> fleet,
                                         @NonNull ScoreStatistics statistics,
                                         boolean opponentSurrendered) {
        return new WinScreen(activity, game, session, fleet, statistics, opponentSurrendered);
    }

    @NonNull
    public static LostScreen newLostScreen(@NonNull Game game, @NonNull Session session) {
        return new LostScreen(activity, game, session);
    }

    @NonNull
    public static RanksListScreen newRanksListScreen() {
        return new RanksListScreen(activity);
    }
}
