package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.common.Command;

/**
 * Created by novikov on 11/24/16.
 */
public abstract class OpponentCommand extends Command {
    @NonNull
    protected final Opponent mOpponent;

    public OpponentCommand(@NonNull Opponent opponent) {
        mOpponent = opponent;
    }
}
