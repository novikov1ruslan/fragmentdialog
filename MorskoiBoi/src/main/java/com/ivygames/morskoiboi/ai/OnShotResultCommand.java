package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.common.Command;

final class OnShotResultCommand extends Command {

    @NonNull
    private final Opponent mOpponent;
    @NonNull
    private final ShotResult mResult;

    OnShotResultCommand(@NonNull Opponent opponent, @NonNull ShotResult result) {
        mOpponent = opponent;
        mResult = result;
    }

    @Override
    public void execute() {
        mOpponent.onShotResult(mResult);
    }

}
