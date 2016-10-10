package com.ivygames.morskoiboi.rt;

import android.support.annotation.NonNull;

import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.morskoiboi.model.Game;

import org.commons.logger.Ln;

public class InternetGame extends Game {
    private static final int TURN_TIMEOUT = 40 * 1000;

    @NonNull
    private final RealTimeMultiplayer mMultiplayer;

    public InternetGame(@NonNull RealTimeMultiplayer multiplayer) {
        mMultiplayer = multiplayer;
    }

    @Override
    public boolean finish() {
        if (super.finish()) {
            return false;
        }

        Ln.d("finishing internet game - leaving the room");
        mMultiplayer.leaveCurrentRoom();
        return true;
    }

    @Override
    public Type getType() {
        return Type.INTERNET;
    }

    @Override
    public int getTurnTimeout() {
        return TURN_TIMEOUT;
    }

}
