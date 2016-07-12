package com.ivygames.morskoiboi;

import android.app.Application;

import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

/**
 * ANDROID_VERSION ﻿﻿APP_VERSION_CODE ﻿﻿APP_VERSION_NAME ﻿﻿PACKAGE_NAME ﻿REPORT_ID ﻿STACK_TRACE ﻿USER_APP_START_DATE ﻿USER_CRASH_DATE
 */
@ReportsCrashes(formUri = "http://collector.tracepot.com/3bca6759", customReportContent = {ReportField.ANDROID_VERSION, ReportField.APP_VERSION_CODE,
        ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME, ReportField.REPORT_ID, ReportField.STACK_TRACE, ReportField.USER_APP_START_DATE,
        ReportField.USER_CRASH_DATE, ReportField.PHONE_MODEL, ReportField.AVAILABLE_MEM_SIZE, ReportField.BRAND, ReportField.BUILD, ReportField.DISPLAY,
        ReportField.USER_COMMENT, ReportField.DEVICE_FEATURES, ReportField.SHARED_PREFERENCES, ReportField.BUILD_CONFIG, ReportField.LOGCAT}, logcatArguments = {
        "-t", "2000", "-v", "time"})
public class BattleshipApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationInitializer.initialize(this);
    }

}
