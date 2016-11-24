package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;

class GoCommand extends OpponentCommand {

    GoCommand(@NonNull Opponent opponent) {
        super(opponent);
    }

    @Override
    public void execute() {
        mOpponent.go();
    }

}
