package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;

public class GoCommand extends OpponentCommand {

    public GoCommand(@NonNull Opponent opponent) {
        super(opponent);
    }

    @Override
    public void execute() {
        mOpponent.go();
    }

}
