package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Opponent;

class GoCommand implements Runnable {

    @NonNull
    private final Opponent mOpponent;

    GoCommand(@NonNull Opponent opponent) {
        mOpponent = opponent;
    }

    @Override
    public void run() {
        mOpponent.go();
    }

    @Override
    public String toString() {
        return GoCommand.class.getSimpleName() + "#" + hashCode();
    }
}
