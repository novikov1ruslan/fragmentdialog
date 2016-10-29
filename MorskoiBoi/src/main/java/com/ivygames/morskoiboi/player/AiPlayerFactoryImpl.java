package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.AiPlayerFactory;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;

public class AiPlayerFactoryImpl implements AiPlayerFactory {

    @NonNull
    private final BotFactory mBotFactory;

    public AiPlayerFactoryImpl(@NonNull BotFactory botFactory) {
        mBotFactory = botFactory;
    }

    @Override
    public AiOpponent createPlayer(@NonNull String name,
                                   @NonNull Placement placement,
                                   @NonNull Rules rules) {
        return new AiOpponent(name, placement, rules, mBotFactory.createBot(), new Bidder());
    }

    @Override
    public String toString() {
        return AiPlayerFactoryImpl.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
