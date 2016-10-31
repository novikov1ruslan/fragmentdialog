package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.AiPlayerFactory;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BidAiPlayerFactory implements AiPlayerFactory {

    private final Random mRandom;
    private final int[] mBid;

    public BidAiPlayerFactory(Random random, int... bid) {
        mRandom = random;
        mBid = bid;
    }

    @Override
    public PlayerOpponent createPlayer(@NonNull String name,
                                       @NonNull Placement placement,
                                       @NonNull Rules rules) {
        BotAlgorithm bot = new MyBotAlgorithm(rules);
        return new BidAiOpponent("ai", placement, rules, bot, mBid, mRandom);
    }

    private class MyBotAlgorithm implements BotAlgorithm {
        private int mCurShot;

        @NonNull
        private final List<Vector2> mShots = new ArrayList<>();

        public MyBotAlgorithm(Rules rules) {
            mShots.addAll(Utils.getShots(rules, mRandom));
            mShots.addAll(Utils.getShots(rules, mRandom));
        }

        @NonNull
        @Override
        public Vector2 shoot(@NonNull Board board) {
            return mShots.get(mCurShot++);
        }
    }
}
