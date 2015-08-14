package com.ivygames.morskoiboi.bluetooth;

import java.io.IOException;

public interface ConnectionListener {

    void onConnected(BluetoothConnection connection);

    /**
     * connection attempt failed
     */
    void onConnectFailed(IOException exception);

}