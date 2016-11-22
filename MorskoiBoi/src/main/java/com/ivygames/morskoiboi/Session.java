package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.player.PlayerOpponent;

public class Session {
    @NonNull
    public final PlayerOpponent player;
    @NonNull
    public final Opponent opponent;

    public Session(@NonNull PlayerOpponent player, @NonNull Opponent opponent) {
        this.player = player;
        this.opponent = opponent;
    }

    public static void bindOpponents(@NonNull Opponent opponent1, @NonNull Opponent opponent2) {
        opponent1.setOpponent(opponent2);
        opponent2.setOpponent(opponent1);
    }
}
