package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;

import java.util.List;

class BidPlayer extends PlayerOpponent {
    private final int[] mBid;
    private int mCurBid;

    protected BidPlayer(@NonNull String name,
                        @NonNull Placement placement,
                        @NonNull Rules rules, int[] bid, PlayerCallback callback) {
        super(name, placement, rules);
        mBid = bid;
        registerCallback(callback);
    }

    @Override
    public void go() {
        super.go();
    }

    @Override
    public void startBidding(int bid) {
        super.startBidding(mBid[mCurBid++]);
    }
}
