package com.ivygames.morskoiboi;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

import org.commons.logger.Ln;

import java.io.IOException;

public class SoundBar {

    private final SoundPool mSoundPool;
    private int mSound;

    public SoundBar(AssetManager assets, String sound) {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (GameSettings.get().isSoundOn()) {
                    float volume = BattleshipApplication.get().getVolume();
                    mSoundPool.play(mSound, volume, volume, 1, 0, 1F);
                }
            }
        });
        try {
            mSound = mSoundPool.load(assets.openFd(sound), 1);
        } catch (IOException e) {
            Ln.w(e);
        }
    }

    public void autoPause() {
        mSoundPool.autoPause();
    }

    public void autoResume() {
        mSoundPool.autoResume();
    }

    public void release() {
        mSoundPool.release();
    }

}
