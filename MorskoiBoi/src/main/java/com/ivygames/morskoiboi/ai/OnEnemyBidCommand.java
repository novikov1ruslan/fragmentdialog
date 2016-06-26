package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Opponent;

class OnEnemyBidCommand implements Runnable {

    @NonNull
    private final Opponent mOpponent;
    private final int mMyBid;

    OnEnemyBidCommand(@NonNull Opponent opponent, int myBid) {
        mOpponent = opponent;
        mMyBid = myBid;
    }

    @Override
    public void run() {
        mOpponent.onEnemyBid(mMyBid);
    }

    @Override
    public String toString() {
        return OnEnemyBidCommand.class.getSimpleName() + "#" + hashCode();
    }
}
