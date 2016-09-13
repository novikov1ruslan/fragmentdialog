package com.ivygames.morskoiboi;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.common.analytics.GoogleAnalyticsInitializer;
import com.ivygames.common.analytics.WarningEvent;
import com.ivygames.common.googleapi.GoogleApiClientWrapper;
import com.ivygames.common.invitations.InvitationManager;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.ai.BotFactory;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.variant.FleetBitmaps;
import com.ivygames.morskoiboi.variant.RussianBot;
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

    public static void initialize(final Application application) {
        Log.v("Battleship", "initializing application...");
        ACRA.init(application);
        AndroidDevice device = new AndroidDevice(application);
        initLogger(application, device.isDebug());
        GoogleAnalyticsInitializer.initAnalytics(application, ANALYTICS_KEY);

        GameSettings settings = new GameSettings(application);

        Resources resources = application.getResources();
        RussianRules rules = new RussianRules();
        Placement algorithm = new Placement(new Random(System.currentTimeMillis()), rules);
        PlacementFactory.setPlacementAlgorithm(algorithm);
        BotFactory.setAlgorithm(new RussianBot(null));

        GoogleApiClientWrapper apiClient = new GoogleApiClientWrapper(application);
        apiClient.setDryRun(BuildConfig.DEBUG);
        ProgressManager progressManager = new ProgressManager(apiClient, settings);
        progressManager.setDryRun(true);

        Dependencies.inject(rules);
        Dependencies.inject(settings);
        Dependencies.inject(apiClient);
        Dependencies.inject(new InvitationManager(apiClient));
        Dependencies.inject(new AchievementsManager(apiClient, settings));
        Dependencies.inject(progressManager);
        Dependencies.inject(device);

        FleetBitmaps fleetBitmapsChooser = new RussianFleetBitmapsChooser();
        Bitmaps.loadBitmaps(fleetBitmapsChooser, resources);

        ExceptionHandler.setDryRun(BuildConfig.DEBUG);
        Ln.v("... application initialization complete");
    }

    private static void initLogger(Application application, boolean isDebug) {
        int minimumLogLevel = isDebug ? Log.VERBOSE : Log.INFO;
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
