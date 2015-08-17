package com.ivygames.morskoiboi.bluetooth;

public interface BluetoothConnection {

    void write(String message);

    void setMessageListener(MessageReceiver listener);

    void disconnect();
}
