package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;

import java.util.List;

class BidPlayer extends PlayerOpponent {
    @NonNull
    private final List<Vector2> mShots;
    private final int mBid;
    private int mCurShot;

    protected BidPlayer(@NonNull String name,
                        @NonNull Placement placement,
                        @NonNull Rules rules, int bid, PlayerCallback callback) {
        super(name, placement, rules);
        mBid = bid;
        mShots = Utils.getShots(rules, placement);
        registerCallback(callback);
    }

    @Override
    public void go() {
        super.go();
        mOpponent.onShotAt(getNextShot());
    }

    @Override
    public void startBidding(int bid) {
        super.startBidding(mBid);
    }

    @NonNull
    private Vector2 getNextShot() {
        return mShots.get(mCurShot++);
    }
}
