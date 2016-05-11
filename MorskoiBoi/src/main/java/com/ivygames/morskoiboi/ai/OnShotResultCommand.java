package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;

final class OnShotResultCommand implements Runnable {

    @NonNull
    private final Opponent mOpponent;
    @NonNull
    private final PokeResult mResult;

    OnShotResultCommand(@NonNull Opponent opponent, @NonNull PokeResult result) {
        mOpponent = opponent;
        mResult = result;
    }

    @Override
    public void run() {
        mOpponent.onShotResult(mResult);
    }

}
