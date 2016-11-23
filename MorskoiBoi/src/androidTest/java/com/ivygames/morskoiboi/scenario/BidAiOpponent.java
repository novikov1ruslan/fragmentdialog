package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ai.AiOpponent;
import com.ivygames.battleship.ai.Bot;
import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.Rules;

import java.util.Random;

public class BidAiOpponent extends AiOpponent {

    private final int[] mBid;
    private int mCurBid;

    public BidAiOpponent(@NonNull String name,
                         @NonNull Rules rules,
                         @NonNull Bot bot, int[] bid,
                         @NonNull Random random) {
        super(name, rules, bot, new Bidder(random), random);
        mBid = bid;
    }

    @Override
    public void startBidding(int bid) {
        super.startBidding(mBid[mCurBid++]);
    }
}
