package com.ivygames.common.music;

import org.commons.logger.Ln;

public class NullSoundBar implements SoundBar {
    @Override
    public void play() {
        Ln.i("dummy");
    }

    @Override
    public void release() {
        Ln.i("dummy");
    }

    @Override
    public void resume() {
        Ln.i("dummy");
    }

    @Override
    public void pause() {
        Ln.i("dummy");
    }
}
