package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.common.Command;
import com.ivygames.morskoiboi.model.Opponent;

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
