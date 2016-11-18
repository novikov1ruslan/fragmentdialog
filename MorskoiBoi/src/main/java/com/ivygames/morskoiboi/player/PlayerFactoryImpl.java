package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.common.DebugUtils;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerFactory;
import com.ivygames.morskoiboi.Rules;

public class PlayerFactoryImpl implements PlayerFactory {
    @Override
    public PlayerOpponent createPlayer(@NonNull String name,
                                              @NonNull Placement placement,
                                              @NonNull Rules rules) {
        return new PlayerOpponent(name, rules);
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
