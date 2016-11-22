package com.ivygames.battleship;

import android.support.annotation.NonNull;

import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;

public interface PlayerFactory {
    PlayerOpponent createPlayer(@NonNull String name,
                                @NonNull Placement placement,
                                @NonNull Rules rules);
}
