package com.ivygames.morskoiboi.bluetooth;

public interface BluetoothConnection {

    void write(String message);

    void setMessageReceiver(MessageReceiver listener);

    void disconnect();
}
