package com.ivygames.morskoiboi;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

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

        int minimumLogLevel = isDebug() ? Log.VERBOSE : Log.INFO;
        String path = getFilesDir().getPath();
        // filesPath = Environment.getExternalStorageDirectory().getPath();
        Config logConfig = new Config(minimumLogLevel, path, "battleship");
        Ln.setConfiguration(logConfig);

        Log.i("Battleship", "created");

        GoogleAnalytics gaInstance = GoogleAnalytics.getInstance(this);
        Tracker tracker = gaInstance.newTracker(GameConstants.ANALYTICS_KEY);
        UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter myHandler = new ExceptionReporter(tracker, exceptionHandler, this);
        // Make myHandler the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);
        myHandler.setExceptionParser(new AnalyticsExceptionParser());

        // EasyTracker.getInstance().setContext(this);
        //
        // // Change uncaught exception parser...
        // // Note: Checking uncaughtExceptionHandler type can be useful if
        // // clearing ga_trackingId during development to disable analytics -
        // // avoid NullPointerException.
        // Thread.UncaughtExceptionHandler uncaughtExceptionHandler =
        // Thread.getDefaultUncaughtExceptionHandler();
        // if (uncaughtExceptionHandler instanceof ExceptionReporter) {
        // ExceptionReporter exceptionReporter = (ExceptionReporter)
        // uncaughtExceptionHandler;
        // exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
        // }

        // load resources
        Bitmaps.getInstance();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private boolean isDebug() {
        int flags;
        try {
            flags = getPackageManager().getApplicationInfo(getPackageName(), 0).flags;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private static class AnalyticsExceptionParser implements ExceptionParser {

        // public String getDescription(String p_thread, Throwable p_throwable)
        // {
        // return "Thread: " + p_thread + ", Exception: " +
        // ExceptionUtils.getStackTrace(p_throwable);
        // }

        @Override
        public String getDescription(String threadName, Throwable throwable) {
            String deviceInfo = "BOARD=" + Build.BOARD + "; BOOTLOADER=" + Build.BOOTLOADER + "; BRAND=" + Build.BRAND + "; CPU_ABI=" + Build.CPU_ABI
                    + "; DEVICE=" + Build.DEVICE + "; DISPLAY=" + Build.DISPLAY + "; HARDWARE=" + Build.HARDWARE + "; HOST=" + Build.HOST + "; ID=" + Build.ID
                    + "; MANUFACTURER=" + Build.MANUFACTURER + "; MODEL=" + Build.MODEL + "; PRODUCT=" + Build.PRODUCT + "; USER=" + Build.USER + "; SDK="
                    + Build.VERSION.SDK_INT;

            return "[" + threadName + "] " + getStackTrace(throwable) + " (" + deviceInfo + ")";
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
