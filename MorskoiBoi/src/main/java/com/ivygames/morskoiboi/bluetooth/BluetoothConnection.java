package com.ivygames.morskoiboi.bluetooth;

import com.ivygames.morskoiboi.bluetooth.BluetoothGame.MessageListener;

public interface BluetoothConnection {

    void write(String message);

    void setMessageListener(MessageListener listener);

    void disconnect();
}
