package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.common.Command;

class GoCommand extends Command {

    @NonNull
    private final Opponent mOpponent;

    GoCommand(@NonNull Opponent opponent) {
        mOpponent = opponent;
    }

    @Override
    public void execute() {
        mOpponent.go();
    }

}
