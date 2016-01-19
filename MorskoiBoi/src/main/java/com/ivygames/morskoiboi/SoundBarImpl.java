package com.ivygames.morskoiboi;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

import org.commons.logger.Ln;

class SoundBarImpl implements SoundBar {

    private final SoundPool mSoundPool;
    private int mSoundId;
    private boolean mReleased;
    private final AssetFileDescriptor mAssetManager;


    SoundBarImpl(AssetFileDescriptor afd) {
        mAssetManager = afd;
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (mReleased) {
                    Ln.d("loaded when already released");
                }
                else {
                    float volume = BattleshipApplication.get().getVolume();
                    mSoundPool.play(mSoundId, volume, volume, 1, 0, 1F);
                }
            }
        });
    }

    @Override
    public void play() {
        mSoundId = mSoundPool.load(mAssetManager, 1);
    }

    @Override
    public void release() {
        mSoundPool.release();
        mReleased = true;
    }

    @Override
    public void resume() {
        mSoundPool.autoResume();
    }

    @Override
    public void pause() {
        mSoundPool.autoPause();
    }

}
