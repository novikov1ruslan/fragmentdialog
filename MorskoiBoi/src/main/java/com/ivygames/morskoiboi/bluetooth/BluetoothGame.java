// FIXME: support android 4.2
package com.ivygames.morskoiboi.bluetooth;

import com.ivygames.morskoiboi.model.Game;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.util.UUID;

public class BluetoothGame extends Game {
    public static final int WIN_PROGRESS_POINTS = 5000;

    private static final int TURN_TIMEOUT = 40 * 1000;

    // Unique UUID for this application
    static final UUID MY_UUID = UUID.fromString("9ecd276e-c044-43ea-969e-2ed67fc9f633");

    private final BluetoothConnection mConnection;

    public BluetoothGame(BluetoothConnection connection) {
        super();
        mConnection = Validate.notNull(connection);
        Ln.v("new bluetooth game created");
    }

    @Override
    public void finish() {
        if (hasFinished()) {
            Ln.w(getType() + " already finished");
            return;
        }

        super.finish();
        mConnection.disconnect();
        Ln.v("game finished");
    }

    @Override
    public Type getType() {
        return Type.BLUETOOTH;
    }

    @Override
    public int getTurnTimeout() {
        return TURN_TIMEOUT;
    }

}
