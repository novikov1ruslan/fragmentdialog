package com.ivygames.morskoiboi.ai;

import org.apache.commons.lang3.Validate;

public class BotFactory {

    private static BotAlgorithm sAlgorithm;

    private BotFactory() {

    }

    public static void setAlgorithm(BotAlgorithm algorithm) {
        sAlgorithm = Validate.notNull(algorithm);
    }

    static BotAlgorithm getAlgorithm() {
        return sAlgorithm;
    }

}
