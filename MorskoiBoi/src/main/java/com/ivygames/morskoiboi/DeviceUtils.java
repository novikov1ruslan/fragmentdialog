package com.ivygames.morskoiboi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.commons.logger.Ln;

import java.util.List;

public class DeviceUtils {

    private static final int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};
    private static GoogleApiAvailability sApiAvailability;

    private DeviceUtils() {
        // utils
    }

    public static void init(GoogleApiAvailability apiAvailability) {
        sApiAvailability = apiAvailability;
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public static boolean canResolveIntent(@NonNull PackageManager pm, @NonNull Intent intent) {
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 0);
        return !resolveInfo.isEmpty();
    }

    public static void printIntent(Intent intent) {
        Ln.v("intent=" + intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            DeviceUtils.printExtras(extras);
        }
    }

    public static boolean isTablet(Resources res) {
        return res.getBoolean(R.bool.is_tablet);
    }

    private static void printExtras(Bundle extras) {
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            if (value == null) {
                Ln.v(key + "= null");
            } else {
                Ln.v(key + "=" + value.toString() + "[" + value.getClass().getName() + "]");
            }
        }
    }

    public static String getVersionName(Resources res) {
        return res.getString(R.string.versionName);
    }

    @NonNull
    public static String getDeviceInfo() {
        return "BOARD=" + Build.BOARD + "; BOOTLOADER=" + Build.BOOTLOADER + "; BRAND=" + Build.BRAND + "; CPU_ABI=" + Build.CPU_ABI
                + "; DEVICE=" + Build.DEVICE + "; DISPLAY=" + Build.DISPLAY + "; HARDWARE=" + Build.HARDWARE + "; HOST=" + Build.HOST + "; ID=" + Build.ID
                + "; MANUFACTURER=" + Build.MANUFACTURER + "; MODEL=" + Build.MODEL + "; PRODUCT=" + Build.PRODUCT + "; USER=" + Build.USER + "; SDK="
                + Build.VERSION.SDK_INT;
    }

    public static boolean isDebug(Context context) {
        int flags;
        try {
            flags = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).flags;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static boolean isVibrationOn(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio.getRingerMode() != AudioManager.RINGER_MODE_SILENT;
    }

    public static boolean isGoogleServicesAvailable(Context context) {
        return sApiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }
}
