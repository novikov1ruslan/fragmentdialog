package com.ivygames.morskoiboi.bluetooth;

import com.ivygames.morskoiboi.bluetooth.BluetoothGame.MessageListener;

public interface MessageSender {

    void write(String message);

    void setMessageListener(MessageListener listener);

}
