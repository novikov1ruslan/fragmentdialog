package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.morskoiboi.Rules;

public interface AiPlayerFactory {
    PlayerOpponent createPlayer(@NonNull String name, @NonNull Rules rules);
}
