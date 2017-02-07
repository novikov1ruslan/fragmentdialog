package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Rules;
import org.commons.logger.LoggerUtils;
import com.ivygames.common.game.Bidder;

import java.util.Random;

public class AiPlayerFactoryImpl implements AiPlayerFactory {

    @NonNull
    private final Bot mBot;
    @NonNull
    private final Random mRandom;

    public AiPlayerFactoryImpl(@NonNull Bot bot, @NonNull Random random) {
        mBot = bot;
        mRandom = random;
    }

    @Override
    public AiOpponent createPlayer(@NonNull String name, @NonNull Rules rules) {
        return new AiOpponent(name, rules, mBot, new Bidder(mRandom), mRandom);
    }

    @Override
    public String toString() {
        return LoggerUtils.getSimpleName(this);
    }
}
