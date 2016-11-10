package com.ivygames.morskoiboi.player;

import com.ivygames.morskoiboi.ai.BotAlgorithm;
import com.ivygames.morskoiboi.variant.RussianBot;

import java.util.Random;

public class RussianBotFactory implements BotFactory {
    @Override
    public BotAlgorithm createBot() {
        return new RussianBot(new Random());
    }

    @Override
    public String toString() {
        return RussianBotFactory.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
