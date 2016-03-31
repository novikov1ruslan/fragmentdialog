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
    private final AudioManager mAudioManager;

    SoundBarImpl(AssetFileDescriptor afd, AudioManager audioManager) {
        mAssetManager = afd;
        mAudioManager = audioManager;
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (mReleased) {
                    Ln.d("loaded when already released");
                }
                else {
                    float volume = getVolume();
                    mSoundPool.play(mSoundId, volume, volume, 1, 0, 1F);
                }
            }
        });
        Ln.d("created music for " + afd);
    }

    @Override
    public void play() {
        mSoundId = mSoundPool.load(mAssetManager, 1);
    }

    @Override
    public void release() {
        mSoundPool.release();
        mReleased = true;
        Ln.d("music released");
    }

    @Override
    public void resume() {
        mSoundPool.autoResume();
        Ln.v("music resumed");
    }

    @Override
    public void pause() {
        mSoundPool.autoPause();
        Ln.v("music paused");
    }

    private float getVolume() {
        float actualVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume / maxVolume;
    }
}
