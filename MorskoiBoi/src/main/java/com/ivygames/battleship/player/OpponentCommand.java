package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.common.Command;

abstract class OpponentCommand extends Command {
    @NonNull
    protected final Opponent mOpponent;

    OpponentCommand(@NonNull Opponent opponent) {
        mOpponent = opponent;
    }
}
