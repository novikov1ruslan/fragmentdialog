package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.NonNull;

public class BluetoothAdapterWrapper {

    @NonNull
    private final BluetoothAdapter mAdapter;

    public BluetoothAdapterWrapper(@NonNull BluetoothAdapter adapter) {
        mAdapter = adapter;
    }

    public boolean isDiscoverable() {
        return mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }
}
