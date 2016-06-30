package com.ivygames.morskoiboi.bluetooth;

import android.support.annotation.NonNull;

public interface ConnectionListener {

    void onConnected(@NonNull BluetoothConnection connection);

    /**
     * connection attempt failed
     */
    void onConnectFailed();

}