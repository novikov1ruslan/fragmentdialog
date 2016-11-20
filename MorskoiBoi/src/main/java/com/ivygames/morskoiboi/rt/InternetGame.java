package com.ivygames.morskoiboi.rt;

import android.support.annotation.NonNull;

import com.ivygames.common.multiplayer.MultiplayerRoom;
import com.ivygames.morskoiboi.model.Game;

import org.commons.logger.Ln;

public class InternetGame extends Game {
    private static final int TURN_TIMEOUT = 40 * 1000;

    @NonNull
    private final MultiplayerRoom mRoom;

    public InternetGame(@NonNull MultiplayerRoom room) {
        mRoom = room;
    }

    @Override
    public boolean finish() {
        if (super.finish()) {
            return false;
        }

        Ln.d("finishing internet game - leaving the room");
        mRoom.leave();
        return true;
    }

    @Override
    public Type getType() {
        return Type.INTERNET;
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
    public String toString() {
        return "[Internet Game]";
    }
}
