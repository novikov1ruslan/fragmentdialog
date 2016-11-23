package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.PlayerFactory;
import com.ivygames.common.DebugUtils;

public class PlayerFactoryImpl implements PlayerFactory {
    @Override
    public PlayerOpponent createPlayer(@NonNull String name, int numberOfShips) {
        return new PlayerOpponent(name, numberOfShips);
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
