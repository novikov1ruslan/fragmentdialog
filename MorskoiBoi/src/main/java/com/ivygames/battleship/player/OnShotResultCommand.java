package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.shot.ShotResult;

public final class OnShotResultCommand extends OpponentCommand {

    @NonNull
    private final ShotResult mResult;

    public OnShotResultCommand(@NonNull Opponent opponent, @NonNull ShotResult result) {
        super(opponent);
        mResult = result;
    }

    @Override
    public void execute() {
        mOpponent.onShotResult(mResult);
    }

}
