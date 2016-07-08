package com.ivygames.common;

import android.bluetooth.BluetoothAdapter;
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

import com.example.novikov.common.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ivygames.common.billing.PurchaseUtils;

import org.commons.logger.Ln;

import java.util.List;

public class AndroidDevice {

    private static final int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};

    @NonNull
    private final Context mContext;

    public AndroidDevice(@NonNull Context context) {
        mContext = context;
    }

    public boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public boolean canResolveIntent(@NonNull Intent intent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 0);
        return !resolveInfo.isEmpty();
    }

    public boolean isTablet() {
        Resources res = mContext.getResources();
        return res.getBoolean(R.bool.is_tablet);
    }

    public static void printIntent(@NonNull Intent intent) {
        Ln.v("intent=" + intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            AndroidDevice.printExtras(extras);
        }
    }

    private static void printExtras(@NonNull Bundle extras) {
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            if (value == null) {
                Ln.v(key + "= null");
            } else {
                Ln.v(key + "=" + value.toString() + "[" + value.getClass().getName() + "]");
            }
        }
    }

    @NonNull
    public String getVersionName() {
        Resources res = mContext.getResources();
        return res.getString(R.string.versionName);
    }

    @NonNull
    public static String getDeviceInfo() {
        return "BOARD=" + Build.BOARD + "; BOOTLOADER=" + Build.BOOTLOADER + "; BRAND=" + Build.BRAND + "; CPU_ABI=" + Build.CPU_ABI
                + "; DEVICE=" + Build.DEVICE + "; DISPLAY=" + Build.DISPLAY + "; HARDWARE=" + Build.HARDWARE + "; HOST=" + Build.HOST + "; ID=" + Build.ID
                + "; MANUFACTURER=" + Build.MANUFACTURER + "; MODEL=" + Build.MODEL + "; PRODUCT=" + Build.PRODUCT + "; USER=" + Build.USER + "; SDK="
                + Build.VERSION.SDK_INT;
    }

    public boolean isDebug() {
        int flags;
        try {
            flags = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), 0).flags;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public boolean isVibrationOn() {
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return audio.getRingerMode() != AudioManager.RINGER_MODE_SILENT;
    }

    public boolean isGoogleServicesAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS;
    }

    public boolean isBillingAvailable() {
        return isGoogleServicesAvailable() && PurchaseUtils.isBillingAvailable(mContext.getPackageManager());
    }

    public boolean hasBluetooth() {
        PackageManager pm = mContext.getPackageManager();
        boolean hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        hasBluetooth &= BluetoothAdapter.getDefaultAdapter() != null;
        return hasBluetooth;
    }

    public boolean bluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }
}
