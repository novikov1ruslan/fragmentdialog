package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.Vector2;

final class OnShootAtCommand implements Runnable {
    @NonNull
    private final Opponent mOpponent;
    @NonNull
    private final Vector2 mAim;

    OnShootAtCommand(@NonNull Opponent opponent, @NonNull Vector2 aim) {
        mOpponent = opponent;
        mAim = aim;
    }

    @Override
    public void run() {
        mOpponent.onShotAt(mAim);
    }

    @Override
    public String toString() {
        return OnShootAtCommand.class.getSimpleName() + mAim;
    }
}
