package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.model.Game;

import org.commons.logger.Ln;

public class AndroidGame extends Game {
    private static final int TURN_TIMEOUT = 2 * 60 * 1000;

    public AndroidGame() {
        Ln.v("new android game created");
    }

    @Override
    public void finish() {
        if (hasFinished()) {
            Ln.w(getType() + " already finished");
            return;
        }

        super.finish();
        Ln.d("finishing Android game - AI stopped");
    }

    @Override
    public Type getType() {
        return Type.VS_ANDROID;
    }

    @Override
    public int getTurnTimeout() {
        return TURN_TIMEOUT;
    }

}
