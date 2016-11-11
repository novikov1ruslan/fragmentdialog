package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.common.DebugUtils;
import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.AiPlayerFactory;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;

import java.util.Random;

public class AiPlayerFactoryImpl implements AiPlayerFactory {

    @NonNull
    private final BotFactory mBotFactory;
    @NonNull
    private final Random mRandom;

    public AiPlayerFactoryImpl(@NonNull BotFactory botFactory, @NonNull Random random) {
        mBotFactory = botFactory;
        mRandom = random;
    }

    @Override
    public AiOpponent createPlayer(@NonNull String name,
                                   @NonNull Placement placement,
                                   @NonNull Rules rules) {
        return new AiOpponent(name, placement, rules, mBotFactory.createBot(), new Bidder(mRandom), mRandom);
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
