package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothAdapterWrapper {

    @NonNull
    private final BluetoothAdapter mAdapter;

    public BluetoothAdapterWrapper(@NonNull BluetoothAdapter adapter) {
        mAdapter = adapter;
    }

    public boolean isDiscoverable() {
        return mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return mAdapter.getBondedDevices();
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return mAdapter.getRemoteDevice(address);
    }

    public void cancelDiscovery() {
        mAdapter.cancelDiscovery();
    }

    public void startDiscovery() {
        mAdapter.startDiscovery();
    }

    public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID myUuid) throws IOException {
        return mAdapter.listenUsingInsecureRfcommWithServiceRecord(name, myUuid);
    }
}
