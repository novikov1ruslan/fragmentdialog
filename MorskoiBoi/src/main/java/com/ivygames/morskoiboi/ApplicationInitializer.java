package com.ivygames.morskoiboi;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.ai.BotFactory;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.GlobalTracker;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.analytics.UiEventImpl;
import com.ivygames.common.analytics.WarningEvent;
import com.ivygames.morskoiboi.invitations.InvitationManager;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.variant.RussianBot;
import com.ivygames.morskoiboi.variant.RussianPlacement;
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

public class ApplicationInitializer {

    public static void initialize(final Application application) {
        ACRA.init(application);
        GameSettings.init(application);

        GoogleApiClientWrapper apiClient = new GoogleApiClientWrapper(application);
        Dependencies.injectApiClient(apiClient);
        Dependencies.injectInvitationManager(new InvitationManager(apiClient));
        Dependencies.injectAchievementsManager(new AchievementsManager(apiClient));
        Dependencies.injectProgressManager(new ProgressManager(apiClient, GameSettings.get()));

        AndroidDeviceFactory.inject(new AndroidDevice(application));

        initLogger(application, AndroidDeviceFactory.getDevice().isDebug());
        initAnalytics(application);

        Bitmaps.getInstance().loadBitmaps(application.getResources());

        // dependency injection
        RulesFactory.setRules(new RussianRules(application.getResources()));
        PlacementFactory.setPlacementAlgorithm(new RussianPlacement(new Random(System.currentTimeMillis()), RulesFactory.getRules().getTotalShips()));
        BotFactory.setAlgorithm(new RussianBot(null));
    }

    private static void initAnalytics(Application application) {
        // Get the GoogleAnalytics singleton. Note that the SDK uses
        // the application context to avoid leaking the current context.
        if (GameConstants.IS_TEST_MODE) {
//            Logger interface is deprecated. Use adb shell setprop log.tag.GAv4 DEBUG to enable debug logging for Google Analytics.
            GoogleAnalytics.getInstance(application).setDryRun(true);
        }
        GlobalTracker.sTracker = GoogleAnalytics.getInstance(application).newTracker(GameConstants.ANALYTICS_KEY);
        GlobalTracker.sTracker.enableAdvertisingIdCollection(true);
        UiEvent.set(new UiEventImpl());
        Log.i("Battleship", "created");

        Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter exceptionReporter = new ExceptionReporter(GlobalTracker.sTracker, exceptionHandler, application);
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
