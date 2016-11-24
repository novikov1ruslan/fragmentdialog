package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Vector;

public final class OnShootAtCommand extends OpponentCommand {
    @NonNull
    private final Vector mAim;

    public OnShootAtCommand(@NonNull Opponent opponent, @NonNull Vector aim) {
        super(opponent);
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
