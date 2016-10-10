package com.ivygames.morskoiboi.bluetooth;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.multiplayer.AbstractOnlineOpponent;

import org.commons.logger.Ln;

public class BluetoothOpponent extends AbstractOnlineOpponent implements MessageReceiver {

    @NonNull
    private final BluetoothConnection mSender;

    public BluetoothOpponent(@NonNull BluetoothConnection sender, @NonNull String defaultName) {
        super(defaultName);
        mSender = sender;
        Ln.v("new bluetooth opponent created");
    }

    @Override
    public void send(@NonNull String message) {
        Ln.v("sending: [" + message + "]");
        message = message + '|';
        mSender.write(message);
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
        String message = NAME + mOpponent.getName();
        send(message);
    }

    @Override
    public void onMessageReceived(@NonNull String message) {
        onRealTimeMessageReceived(message);
        Ln.v("received [" + message + "] from [" + getName() + "]");
    }

}
