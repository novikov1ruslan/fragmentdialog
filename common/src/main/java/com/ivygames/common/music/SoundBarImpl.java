package com.ivygames.common.music;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

class SoundBarImpl implements SoundBar {

    @NonNull
    private final SoundPool mSoundPool;
    private int mSoundId;
    private boolean mReleased;

    @NonNull
    private final AssetFileDescriptor mAfd;
    @NonNull
    private final AudioManager mAudioManager;

    SoundBarImpl(@NonNull AssetFileDescriptor afd, @NonNull AudioManager audioManager) {
        mAfd = afd;
        mAudioManager = audioManager;
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (mReleased) {
                    Ln.w("loaded when already released, not playing");
                }
                else {
                    float volume = getVolume();
                    mSoundPool.play(mSoundId, volume, volume, 1, 0, 1F);
                }
            }
        });
        Ln.v("created music for " + afd);
    }

    @Override
    public void play() {
        mSoundId = mSoundPool.load(mAfd, 1);
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
