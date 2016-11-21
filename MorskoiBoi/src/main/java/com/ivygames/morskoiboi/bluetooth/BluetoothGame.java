// FIXME: support android 4.2
package com.ivygames.morskoiboi.bluetooth;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Game;

import org.commons.logger.Ln;

import java.util.UUID;

public class BluetoothGame extends Game {

    private static final int TURN_TIMEOUT = 40 * 1000;

    // Unique UUID for this application
    static final UUID MY_UUID = UUID.fromString("9ecd276e-c044-43ea-969e-2ed67fc9f633");

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
    public Type getType() {
        return Type.BLUETOOTH;
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
    public boolean hasSetupTimeout() {
        return false;
    }

    @Override
    public boolean supportsAchievements() {
        return false;
    }

    @Override
    public String toString() {
        return "[Bluetooth Game]";
    }
}
