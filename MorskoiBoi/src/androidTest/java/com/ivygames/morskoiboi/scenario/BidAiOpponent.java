package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.player.AiOpponent;

public class BidAiOpponent extends AiOpponent {

    private final int[] mBid;
    private int mCurBid;

    public BidAiOpponent(@NonNull String name,
                         @NonNull Placement placement,
                         @NonNull Rules rules,
                         @NonNull BotAlgorithm bot, int[] bid) {
        super(name, placement, rules, bot, new Bidder());
        mBid = bid;
    }

    @Override
    public void startBidding(int bid) {
        super.startBidding(mBid[mCurBid++]);
    }
}
