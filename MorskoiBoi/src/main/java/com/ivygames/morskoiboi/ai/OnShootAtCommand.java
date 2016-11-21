package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Vector2;
import com.ivygames.common.Command;

final class OnShootAtCommand extends Command {
    @NonNull
    private final Opponent mOpponent;
    @NonNull
    private final Vector2 mAim;

    OnShootAtCommand(@NonNull Opponent opponent, @NonNull Vector2 aim) {
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
