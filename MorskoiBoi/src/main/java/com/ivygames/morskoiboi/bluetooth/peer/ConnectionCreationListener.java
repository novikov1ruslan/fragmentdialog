package com.ivygames.morskoiboi.bluetooth.peer;

import android.support.annotation.NonNull;

public interface ConnectionCreationListener {

    void onConnected(@NonNull BluetoothConnection connection);

    /**
     * connection attempt failed
     */
    void onConnectFailed();

}