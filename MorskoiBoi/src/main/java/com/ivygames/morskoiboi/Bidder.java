package com.ivygames.morskoiboi;

import java.util.Random;

public class Bidder {
    private final Random mRandom = new Random();
    public int newBid() {
        mRandom.setSeed(System.currentTimeMillis() + hashCode());
        return mRandom.nextInt(Integer.MAX_VALUE);
    }
}
