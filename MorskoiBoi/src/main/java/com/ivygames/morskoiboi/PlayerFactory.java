package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.player.PlayerOpponent;

public interface PlayerFactory {

    PlayerOpponent createPlayer(@NonNull String name,
                                       @NonNull Placement placement,
                                       @NonNull Rules rules);
}
