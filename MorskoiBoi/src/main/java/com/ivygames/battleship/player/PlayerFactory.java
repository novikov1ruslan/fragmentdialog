package com.ivygames.battleship.player;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.commons.logger.LoggerUtils;

import org.commons.logger.Ln;

public class PlayerFactory {

    @NonNull
    private final String mDefaultName;

    public PlayerFactory(@NonNull String defaultName) {
        mDefaultName = defaultName;
    }

    public PlayerOpponent createPlayer(@NonNull String name, int numberOfShips) {
        if (TextUtils.isEmpty(name)) {
            name = mDefaultName;
            Ln.i("player name is empty - replaced by " + name);
        }
        return new PlayerOpponent(name, numberOfShips);
    }

    @Override
    public String toString() {
        return LoggerUtils.getSimpleName(this);
    }
}
