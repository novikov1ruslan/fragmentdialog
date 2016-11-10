package com.ivygames.morskoiboi;

import android.app.Application;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.common.analytics.GoogleAnalyticsInitializer;
import com.ivygames.common.analytics.WarningEvent;
import com.ivygames.common.googleapi.GoogleApiClientWrapper;
import com.ivygames.common.multiplayer.MultiplayerImpl;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.player.AiPlayerFactoryImpl;
import com.ivygames.morskoiboi.player.PlayerFactoryImpl;
import com.ivygames.morskoiboi.player.RussianBotFactory;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.variant.FleetBitmaps;
import com.ivygames.morskoiboi.variant.RussianFleetBitmapsChooser;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.acra.ACRA;
import org.commons.logger.Config;
import org.commons.logger.Ln;
import org.commons.logger.Logger;
import org.commons.logger.LoggerImpl;
import org.commons.logger.WarningListener;

import java.util.Random;

class ApplicationInitializer {

    private static final String ANALYTICS_KEY = "UA-43473473-1";

    public static void initialize(@NonNull Application application) {
        Log.v("Battleship", "initializing application...");
        ACRA.init(application);
        initLogger(application);

        GoogleAnalyticsInitializer.initAnalytics(application, ANALYTICS_KEY);

        GameSettings settings = new GameSettings(application);
        Resources resources = application.getResources();
        RussianRules rules = new RussianRules();
        Random random = new Random();
        Placement placement = new Placement(random, rules);
        AndroidDevice device = new AndroidDevice(application);

        GoogleApiClientWrapper apiClient = new GoogleApiClientWrapper(application,
                BattleshipActivity.RC_SIGN_IN, application.getString(R.string.error),
                BattleshipActivity.SERVICE_RESOLVE);
        ProgressManager progressManager = new ProgressManager(apiClient, settings);
        AchievementsManager achievementsManager = new AchievementsManager(apiClient, settings);
        MultiplayerImpl multiplayerManager = new MultiplayerImpl(apiClient,
                BattleshipActivity.RC_WAITING_ROOM);
        PlayerFactory playerFactory = new PlayerFactoryImpl();
        AiPlayerFactory aiPlayerFactory = new AiPlayerFactoryImpl(new RussianBotFactory(), random);

        Dependencies.inject(random);
        Dependencies.inject(placement);
        Dependencies.inject(apiClient);
        Dependencies.inject(multiplayerManager);
        Dependencies.inject(rules);
        Dependencies.inject(settings);
        Dependencies.inject(achievementsManager);
        Dependencies.inject(progressManager);
        Dependencies.inject(device);
        Dependencies.inject(playerFactory);
        Dependencies.inject(aiPlayerFactory);

        FleetBitmaps fleetBitmapsChooser = new RussianFleetBitmapsChooser();
        Bitmaps.loadBitmaps(fleetBitmapsChooser, resources);

        ExceptionHandler.setDryRun(BuildConfig.DEBUG);
        Ln.v("... application initialization complete");
    }

    private static void initLogger(Application application) {
        int minimumLogLevel = BuildConfig.DEBUG ? Log.VERBOSE : Log.INFO;
        String path = application.getFilesDir().getPath();
        // filesPath = Environment.getExternalStorageDirectory().getPath();
        Config logConfig = new Config(minimumLogLevel, path, "battleship");
        WarningListener warningListener = new WarningListener() {

            @Override
            public void onWaring(String message, int level) {
                if (level == Log.WARN) {
                    WarningEvent.send(message);
                } else {
                    ExceptionEvent.send(message);
                }
            }
        };
        Logger logger = new LoggerImpl(logConfig, warningListener);
        Ln.injectLogger(logger);
    }

}
