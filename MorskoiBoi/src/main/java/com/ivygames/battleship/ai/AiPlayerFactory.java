package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.PlayerFactory;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;

public interface AiPlayerFactory extends PlayerFactory {
    @Override
    PlayerOpponent createPlayer(@NonNull String name,
                                @NonNull Placement placement,
                                @NonNull Rules rules);
}
