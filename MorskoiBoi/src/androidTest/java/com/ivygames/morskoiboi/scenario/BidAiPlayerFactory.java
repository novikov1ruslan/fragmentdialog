package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ai.AiPlayerFactory;
import com.ivygames.battleship.ai.Bot;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coordinate;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.morskoiboi.Rules;

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
                                       @NonNull Rules rules) {
        Bot bot = new MyBot(rules);
        return new BidAiOpponent("ai", rules, bot, mBid, mRandom);
    }

    private class MyBot implements Bot {
        private int mCurShot;

        @NonNull
        private final List<Coordinate> mShots = new ArrayList<>();

        public MyBot(Rules rules) {
            mShots.addAll(Utils.getShots(rules, mRandom));
            mShots.addAll(Utils.getShots(rules, mRandom));
        }

        @NonNull
        @Override
        public Coordinate shoot(@NonNull Board board) {
            return mShots.get(mCurShot++);
        }
    }
}
