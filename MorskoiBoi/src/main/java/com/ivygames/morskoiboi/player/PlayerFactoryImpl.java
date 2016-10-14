package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerFactory;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.player.PlayerOpponent;

public class PlayerFactoryImpl implements PlayerFactory {
    @Override
    public PlayerOpponent createPlayer(@NonNull String name,
                                              @NonNull Placement placement,
                                              @NonNull Rules rules) {
        return new PlayerOpponent(name, placement, rules);
    }
}
