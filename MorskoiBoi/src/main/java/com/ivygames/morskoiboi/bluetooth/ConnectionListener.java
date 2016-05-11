package com.ivygames.morskoiboi.bluetooth;

import android.support.annotation.NonNull;

import java.io.IOException;

public interface ConnectionListener {

    void onConnected(@NonNull BluetoothConnection connection);

    /**
     * connection attempt failed
     */
    void onConnectFailed(@NonNull IOException exception);

}