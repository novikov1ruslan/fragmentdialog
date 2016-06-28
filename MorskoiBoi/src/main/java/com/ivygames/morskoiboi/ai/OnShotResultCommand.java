package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.common.Command;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;

final class OnShotResultCommand extends Command {

    @NonNull
    private final Opponent mOpponent;
    @NonNull
    private final PokeResult mResult;

    OnShotResultCommand(@NonNull Opponent opponent, @NonNull PokeResult result) {
        mOpponent = opponent;
        mResult = result;
    }

    @Override
    public void execute() {
        mOpponent.onShotResult(mResult);
    }

    @Override
    public String toString() {
        return OnShotResultCommand.class.getSimpleName() + "#" + hashCode();
    }

}
