package com.ivygames.common.game;

import android.support.annotation.NonNull;

import java.util.Random;

public class Bidder {
    @NonNull
    private final Random mRandom;

    public Bidder(@NonNull Random random) {
        mRandom = random;
    }

    public int newBid() {
        mRandom.setSeed(System.currentTimeMillis() + hashCode());
        return mRandom.nextInt(Integer.MAX_VALUE);
    }
}
