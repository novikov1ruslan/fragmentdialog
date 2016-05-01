package com.ivygames.morskoiboi.screen.gameplay;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import java.io.IOException;
import java.util.Random;

public class GameplaySoundManager {

    private static final int HIT_SOUNDS_COUNT = 2;
    private static final int KILL_SOUNDS_COUNT = 2;
    private static final int SPLASH_SOUNDS_COUNT = 3;

    // audio stuff
    private final SoundPool mSoundPool;
    private final int[] mHitSounds;
    private final int[] mKillSounds;
    private final int[] mSplashSounds;
    private int mKantropSound;
    private int mWhistleSound;
    private int mAlarmSound;

    private volatile int mAlarmStream;
    private int mKantropStream;
    private int mWhistleStream;
    private int mSplashStream;
    private int mHitStream;
    private int mKillStream;

    private final Random mRandom = new Random(System.currentTimeMillis());
    private final AudioManager mAudioManager;

    public GameplaySoundManager(@NonNull AudioManager audioManager) {
        mAudioManager = audioManager;
        int soundsCount = HIT_SOUNDS_COUNT + KILL_SOUNDS_COUNT + SPLASH_SOUNDS_COUNT;
        mSoundPool = new SoundPool(soundsCount, AudioManager.STREAM_MUSIC, 0);

        mHitSounds = new int[HIT_SOUNDS_COUNT];
        mKillSounds = new int[KILL_SOUNDS_COUNT];
        mSplashSounds = new int[SPLASH_SOUNDS_COUNT];
    }

    public void prepareSoundPool(AssetManager assets) {

        try {
            mHitSounds[0] = mSoundPool.load(assets.openFd("hit1.ogg"), 1);
            mHitSounds[1] = mSoundPool.load(assets.openFd("hit2.ogg"), 1);
            mKillSounds[0] = mSoundPool.load(assets.openFd("kill1.ogg"), 1);
            mKillSounds[1] = mSoundPool.load(assets.openFd("kill2.ogg"), 1);
            mSplashSounds[0] = mSoundPool.load(assets.openFd("splash1.ogg"), 1);
            mSplashSounds[1] = mSoundPool.load(assets.openFd("splash2.ogg"), 1);
            mSplashSounds[2] = mSoundPool.load(assets.openFd("splash3.mp3"), 1);
            mKantropSound = mSoundPool.load(assets.openFd("kantrop.ogg"), 1);
            mWhistleSound = mSoundPool.load(assets.openFd("whistle_short.ogg"), 1);
            mAlarmSound = mSoundPool.load(assets.openFd("alarm.mp3"), 1);
        } catch (IOException ioe) {
            Ln.w(ioe);
        }
        Ln.d("sounds loaded");
    }

//    public void autoPause() {
//        mSoundPool.autoPause();
//    }
//
//    public void autoResume() {
//        mSoundPool.autoResume();
//    }

    public void stopPlaying() {
        mSoundPool.stop(mAlarmStream);
        mSoundPool.stop(mKantropStream);

        mSoundPool.stop(mWhistleStream);
        mSoundPool.stop(mSplashStream);
        mSoundPool.stop(mHitStream);
        mSoundPool.stop(mKillStream);
    }

    public void release() {
        mSoundPool.release();
        Ln.v("sounds released");
    }

    public void playWhistleSound() {
        float volume = mRandom.nextFloat() * 0.7f + 0.3f;
        float rate = mRandom.nextFloat() * 0.7f + 0.8f;
        int sound = this.mWhistleSound;
        int loop = 0;
        mWhistleStream = play(sound, volume, loop, rate);
    }

    public void playKantrop() {
        float volume = 0.5f;
        mKantropStream = play(mKantropSound, volume, -1, 1f);
    }

    public void stopKantropSound() {
        mSoundPool.stop(mKantropStream);
    }

    public void playSplash() {
        float volume = getVolume() * 0.2f;
        mSplashStream = play(mSplashSounds[random(SPLASH_SOUNDS_COUNT)], volume, 0, 1f);
    }

    public void playHitSound() {
        float volume = getVolume();
        mHitStream = play(mHitSounds[random(HIT_SOUNDS_COUNT)], volume, 0, 1f);
    }

    public void playKillSound() {
        float volume = getVolume();
        mKillStream = play(mKillSounds[random(KILL_SOUNDS_COUNT)], volume, 0, 1f);
    }

    public boolean isAlarmPlaying() {
        return mAlarmStream != 0;
    }

    public void playAlarmSound() {
        float volume = 0.3f;
        mAlarmStream = play(mAlarmSound, volume, 0, 1f);
    }

    public void stopAlarmSound() {
        mSoundPool.stop(mAlarmStream);
        mAlarmStream = 0;
    }

    private int play(int sound, float volume, int loop, float rate) {
        return mSoundPool.play(sound, volume, volume, 1, loop, rate);
    }

    private int random(int count) {
        return mRandom.nextInt(count);
    }

    private float getVolume() {
        float actualVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume / maxVolume;
    }
}
