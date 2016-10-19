package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.AiPlayerFactory;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;

import java.util.List;

class BidAiPlayerFactory implements AiPlayerFactory {

    private final int mBid;

    public BidAiPlayerFactory(int bid) {
        mBid = bid;
    }

    @Override
    public PlayerOpponent createPlayer(@NonNull String name,
                                       @NonNull final Placement placement,
                                       @NonNull final Rules rules) {
        BotAlgorithm bot = new MyBotAlgorithm(rules, placement);
        return new BidAiOpponent("ai", placement, rules, bot, mBid);
    }

    private class MyBotAlgorithm implements BotAlgorithm {
        private int mCurShot;

        @NonNull
        private final List<Vector2> mShots;

        public MyBotAlgorithm(Rules rules, Placement placement) {
            mShots = Utils.getShots(rules, placement);
        }

        @NonNull
        @Override
        public Vector2 shoot(@NonNull Board board) {
            return mShots.get(mCurShot++);
        }
    }
}
