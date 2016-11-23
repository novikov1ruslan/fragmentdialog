package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Coordinate;
import com.ivygames.common.Command;

final class OnShootAtCommand extends Command {
    @NonNull
    private final Opponent mOpponent;
    @NonNull
    private final Coordinate mAim;

    OnShootAtCommand(@NonNull Opponent opponent, @NonNull Coordinate aim) {
        mOpponent = opponent;
        mAim = aim;
    }

    @Override
    public void execute() {
        mOpponent.onShotAt(mAim);
    }

    @Override
    public String toString() {
        return super.toString() + mAim;
    }
}
