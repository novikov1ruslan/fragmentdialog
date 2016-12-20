// FIXME: support android 4.2
package com.ivygames.morskoiboi.bluetooth;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.bluetooth.peer.BluetoothConnection;

import org.commons.logger.Ln;

public class BluetoothGame extends Game {

    public static final int BLUETOOTH_WIN_POINTS = 5000;
    private static final int TURN_TIMEOUT = 40 * 1000;

    @NonNull
    private final BluetoothConnection mConnection;

    public BluetoothGame(@NonNull BluetoothConnection connection) {
        super();
        mConnection = connection;
        Ln.v("new bluetooth game created");
    }

    @Override
    public boolean finish() {
        if (super.finish()) {
            return true;
        }

        mConnection.disconnect();
        Ln.v("game finished");
        return true;
    }

    @Override
    public boolean shouldNotifyOpponent() {
        return true;
    }

    @Override
    public int getTurnTimeout() {
        return TURN_TIMEOUT;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean supportsAchievements() {
        return false;
    }

    @Override
    public int getWinPoints() {
        return BLUETOOTH_WIN_POINTS;
    }

    @Override
    public boolean isPausable() {
        return false;
    }

    @Override
    public String toString() {
        return "[Bluetooth Game]";
    }
}
