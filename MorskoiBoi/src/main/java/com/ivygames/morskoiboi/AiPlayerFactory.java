package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.player.AiOpponent;
import com.ivygames.morskoiboi.player.PlayerOpponent;

public interface AiPlayerFactory {
    PlayerOpponent createPlayer(@NonNull String name,
                                @NonNull Placement placement,
                                @NonNull Rules rules);
}
