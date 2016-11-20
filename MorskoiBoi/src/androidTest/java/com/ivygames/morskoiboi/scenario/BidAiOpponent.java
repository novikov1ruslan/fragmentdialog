package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.Bot;
import com.ivygames.morskoiboi.player.AiOpponent;

import java.util.Random;

public class BidAiOpponent extends AiOpponent {

    private final int[] mBid;
    private int mCurBid;

    public BidAiOpponent(@NonNull String name,
                         @NonNull Placement placement,
                         @NonNull Rules rules,
                         @NonNull Bot bot, int[] bid,
                         @NonNull Random random) {
        super(name, placement, rules, bot, new Bidder(random), random);
        mBid = bid;
    }

    @Override
    public void startBidding(int bid) {
        super.startBidding(mBid[mCurBid++]);
    }
}
