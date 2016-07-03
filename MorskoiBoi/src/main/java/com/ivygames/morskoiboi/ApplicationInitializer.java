package com.ivygames.morskoiboi;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.common.analytics.GlobalTracker;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.analytics.UiEventImpl;
import com.ivygames.common.analytics.WarningEvent;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.ai.BotFactory;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.invitations.InvitationManager;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

class ApplicationInitializer {

    private static final String ANALYTICS_KEY = "UA-43473473-1";

    public static void initialize(final Application application) {
        ACRA.init(application);
        AndroidDevice device = new AndroidDevice(application);
        initLogger(application, device.isDebug());
        initAnalytics(application);

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
    }

    private static void initAnalytics(Application application) {
        // Get the GoogleAnalytics singleton. Note that the SDK uses
        // the application context to avoid leaking the current context.
        // Logger interface is deprecated. Use adb shell setprop log.tag.GAv4 DEBUG to enable debug logging for Google Analytics.
        GoogleAnalytics.getInstance(application).setDryRun(BuildConfig.DEBUG);
        GlobalTracker.tracker = GoogleAnalytics.getInstance(application).newTracker(ANALYTICS_KEY);
        GlobalTracker.tracker.enableAdvertisingIdCollection(true);
        UiEvent.set(new UiEventImpl());
        Log.i("Battleship", "created");

        Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter exceptionReporter = new ExceptionReporter(GlobalTracker.tracker, exceptionHandler, application);
        // TODO: Make exceptionReporter the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(exceptionReporter);
        exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
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

    private static class AnalyticsExceptionParser implements ExceptionParser {

        @Override
        public String getDescription(String threadName, Throwable throwable) {
            return "[" + threadName + "] " + getStackTrace(throwable) + " (" + AndroidDevice.getDeviceInfo() + ")";
        }

        private String getStackTrace(Throwable throwable) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            throwable.printStackTrace(printWriter);

            return result.toString();
        }
    }

}
