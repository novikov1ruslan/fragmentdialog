package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;

import java.util.List;

class AiBidPlayer extends PlayerOpponent {
    @NonNull
    private final List<Vector2> mShots;
    private final int mBid;
    int mCurShot;

    protected AiBidPlayer(@NonNull String name,
                          @NonNull Placement placement,
                          @NonNull Rules rules, int bid) {
        super(name, placement, rules);
        mBid = bid;
        mShots = Utils.getShots(rules, placement);
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
