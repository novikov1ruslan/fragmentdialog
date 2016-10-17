package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.player.AiOpponent;

public class BidAiOpponent extends AiOpponent {

    private final int mBid;

    public BidAiOpponent(@NonNull String name,
                         @NonNull Placement placement,
                         @NonNull Rules rules, int bid) {
        super(name, placement, rules);
        mBid = bid;
    }

    @Override
    public void startBidding(int bid) {
        super.startBidding(mBid);
    }
}
