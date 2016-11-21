package com.ivygames.battleship.ai;

import com.ivygames.common.DebugUtils;

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
