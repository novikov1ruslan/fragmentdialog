package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.player.AiOpponent;

public interface AiPlayerFactory {
    AiOpponent createPlayer(@NonNull String name,
                            @NonNull Placement placement,
                            @NonNull Rules rules);
}
