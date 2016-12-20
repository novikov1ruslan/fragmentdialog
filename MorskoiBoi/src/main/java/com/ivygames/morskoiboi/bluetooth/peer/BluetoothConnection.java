package com.ivygames.morskoiboi.bluetooth.peer;

public interface BluetoothConnection {

    void write(String message);

    void setMessageReceiver(MessageReceiver listener);

    void disconnect();
}
