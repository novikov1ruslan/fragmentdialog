package com.ivygames.morskoiboi.bluetooth;

public class BluetoothUtils {

	private BluetoothUtils() {

	}

	/**
	 * Get the device MAC address, which is the last 17 chars in the info
	 */
	public static String extractMacAddress(String info) {
		return info.substring(info.length() - 17);
	}
}
