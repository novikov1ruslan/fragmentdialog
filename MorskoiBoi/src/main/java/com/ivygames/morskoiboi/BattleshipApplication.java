package com.ivygames.morskoiboi;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.ivygames.morskoiboi.ai.BotFactory;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.analytics.GlobalTracker;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.analytics.UiEventImpl;
import com.ivygames.morskoiboi.analytics.WarningEvent;
import com.ivygames.morskoiboi.variant.RussianBot;
import com.ivygames.morskoiboi.variant.RussianPlacement;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;
import org.commons.logger.Config;
import org.commons.logger.Ln;
import org.commons.logger.Logger;
import org.commons.logger.LoggerImpl;
import org.commons.logger.WarningListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Random;

/**
 * ANDROID_VERSION ﻿﻿APP_VERSION_CODE ﻿﻿APP_VERSION_NAME ﻿﻿PACKAGE_NAME ﻿REPORT_ID ﻿STACK_TRACE ﻿USER_APP_START_DATE ﻿USER_CRASH_DATE
 */
@ReportsCrashes(formUri = "http://collector.tracepot.com/3bca6759", customReportContent = {ReportField.ANDROID_VERSION, ReportField.APP_VERSION_CODE,
        ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME, ReportField.REPORT_ID, ReportField.STACK_TRACE, ReportField.USER_APP_START_DATE,
        ReportField.USER_CRASH_DATE, ReportField.PHONE_MODEL, ReportField.AVAILABLE_MEM_SIZE, ReportField.BRAND, ReportField.BUILD, ReportField.DISPLAY,
        ReportField.USER_COMMENT, ReportField.DEVICE_FEATURES, ReportField.SHARED_PREFERENCES, ReportField.BUILD_CONFIG, ReportField.LOGCAT}, logcatArguments = {
        "-t", "2000", "-v", "time"})
public class BattleshipApplication extends Application {

    private AudioManager mAudioManager;
    private static BattleshipApplication sContext;

    /**
     * @return global application context
     */
    public static BattleshipApplication get() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        ACRA.init(this);

        initLogger();
        initAnalytics();


        Bitmaps.getInstance().loadBitmaps(getResources());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // dependency injection
        RulesFactory.setRules(new RussianRules());
        PlacementFactory.setPlacementAlgorithm(new RussianPlacement(new Random(System.currentTimeMillis()), RulesFactory.getRules().getTotalShips()));
        BotFactory.setAlgorithm(new RussianBot(null));
    }

    public void initAnalytics() {
        // Get the GoogleAnalytics singleton. Note that the SDK uses
        // the application context to avoid leaking the current context.
        if (GameConstants.IS_TEST_MODE) {
//            Logger interface is deprecated. Use adb shell setprop log.tag.GAv4 DEBUG to enable debug logging for Google Analytics.
            GoogleAnalytics.getInstance(BattleshipApplication.get()).setDryRun(true);
        }
        GlobalTracker.sTracker = GoogleAnalytics.getInstance(this).newTracker(GameConstants.ANALYTICS_KEY);
        GlobalTracker.sTracker.enableAdvertisingIdCollection(true);
        UiEvent.set(new UiEventImpl());
        Log.i("Battleship", "created");

        UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter exceptionReporter = new ExceptionReporter(GlobalTracker.sTracker, exceptionHandler, this);
        // TODO: Make exceptionReporter the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(exceptionReporter);
        exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
    }

    private void initLogger() {
        int minimumLogLevel = DeviceUtils.isDebug(this) ? Log.VERBOSE : Log.INFO;
        String path = getFilesDir().getPath();
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
            return "[" + threadName + "] " + getStackTrace(throwable) + " (" + DeviceUtils.getDeviceInfo() + ")";
        }

        private String getStackTrace(Throwable throwable) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            throwable.printStackTrace(printWriter);

            return result.toString();
        }
    }

    public float getVolume() {
        float actualVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume / maxVolume;
    }

}
