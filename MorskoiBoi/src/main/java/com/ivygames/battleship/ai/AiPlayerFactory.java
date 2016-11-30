package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Rules;
import com.ivygames.battleship.player.PlayerOpponent;

public interface AiPlayerFactory {
    PlayerOpponent createPlayer(@NonNull String name, @NonNull Rules rules);
}
