package com.ivygames.morskoiboi;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ivygames.morskoiboi.ai.BotFactory;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.variant.RussianBot;
import com.ivygames.morskoiboi.variant.RussianPlacement;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;
import org.commons.logger.Ln;
import org.commons.logger.Ln.Config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

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
    public void onCreate() {
        super.onCreate();
        sContext = this;
        ACRA.init(this);

        int minimumLogLevel = DeviceUtils.isDebug(this) ? Log.VERBOSE : Log.INFO;
        String path = getFilesDir().getPath();
        // filesPath = Environment.getExternalStorageDirectory().getPath();
        Config logConfig = new Config(minimumLogLevel, path, "battleship");
        Ln.setConfiguration(logConfig);

        Log.i("Battleship", "created");

        GoogleAnalytics gaInstance = GoogleAnalytics.getInstance(this);
        Tracker tracker = gaInstance.newTracker(GameConstants.ANALYTICS_KEY);
        UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter myHandler = new ExceptionReporter(tracker, exceptionHandler, this);
        // TODO: Make myHandler the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);
        myHandler.setExceptionParser(new AnalyticsExceptionParser());

        Bitmaps.getInstance().loadBitmaps(getResources());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // dependency injection
        PlacementFactory.setPlacementAlgorithm(new RussianPlacement());
        RulesFactory.setRules(new RussianRules());
//        PlacementFactory.setPlacementAlgorithm(new AmericanPlacement());
//        RulesFactory.setRules(new AmericanRules());
        BotFactory.setAlgorithm(new RussianBot());
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
