package com.ivygames.morskoiboi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import org.commons.logger.Ln;

import java.util.List;

public class DeviceUtils {

	private static final int[] NETWORK_TYPES = { ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET };

	private DeviceUtils() {
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

	public static boolean resolverAvailableForIntent(Intent intent) {
		PackageManager pm = BattleshipApplication.get().getPackageManager();
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
			Ln.v(key + "=" + value.toString() + "[" + value.getClass().getName() + "]");
		}
	}
}
