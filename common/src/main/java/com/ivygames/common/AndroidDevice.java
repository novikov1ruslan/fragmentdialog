package com.ivygames.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ivygames.common.billing.PurchaseUtils;

import org.commons.logger.LoggerUtils;

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

    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public boolean canResolveIntent(@NonNull Intent intent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 0);
        return !resolveInfo.isEmpty();
    }

    public boolean isTablet() {
        return mContext.getResources().getBoolean(R.bool.is_tablet);
    }

    @NonNull
    public String getVersionName() {
        return mContext.getResources().getString(R.string.versionName);
    }

    @NonNull
    public static String getDeviceInfo() {
        return "BOARD=" + Build.BOARD + "; BOOTLOADER=" + Build.BOOTLOADER + "; BRAND=" + Build.BRAND + "; CPU_ABI=" + Build.CPU_ABI
                + "; DEVICE=" + Build.DEVICE + "; DISPLAY=" + Build.DISPLAY + "; HARDWARE=" + Build.HARDWARE + "; HOST=" + Build.HOST + "; ID=" + Build.ID
                + "; MANUFACTURER=" + Build.MANUFACTURER + "; MODEL=" + Build.MODEL + "; PRODUCT=" + Build.PRODUCT + "; USER=" + Build.USER + "; SDK="
                + Build.VERSION.SDK_INT;
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

    public BluetoothAdapter getBluetoothAdapter() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            BluetoothManager bm = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            return bm.getAdapter();
        }

        return BluetoothAdapter.getDefaultAdapter();
    }

    public boolean hasBluetooth() {
        PackageManager pm = mContext.getPackageManager();
        boolean hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        hasBluetooth &= getBluetoothAdapter() != null;
        return hasBluetooth;
    }

    public boolean bluetoothEnabled() {
        return getBluetoothAdapter().isEnabled();
    }

    @Override
    public String toString() {
        return LoggerUtils.getSimpleName(this);
    }
}
