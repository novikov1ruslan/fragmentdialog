package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.common.DebugUtils;

public class PlayerFactory {

    public PlayerOpponent createPlayer(@NonNull String name, int numberOfShips) {
        return new PlayerOpponent(name, numberOfShips);
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
