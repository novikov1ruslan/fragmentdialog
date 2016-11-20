package com.ivygames.morskoiboi.player;

import com.ivygames.common.DebugUtils;
import com.ivygames.morskoiboi.ai.Bot;
import com.ivygames.morskoiboi.variant.RussianBot;

import java.util.Random;

public class RussianBotFactory implements BotFactory {
    @Override
    public Bot createBot() {
        return new RussianBot(new Random());
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
