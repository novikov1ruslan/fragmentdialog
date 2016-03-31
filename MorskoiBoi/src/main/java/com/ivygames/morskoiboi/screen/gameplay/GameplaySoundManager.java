package com.ivygames.morskoiboi.screen.gameplay;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.screen.BattleshipScreen;

import org.commons.logger.Ln;

import java.io.IOException;
import java.util.Random;

public class GameplaySoundManager {
    public static final int ALARM_TIME_SECONDS = 10;

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
    private final BattleshipScreen mScreen;
    private final AudioManager mAudioManager;

    public GameplaySoundManager(@NonNull BattleshipScreen screen, @NonNull AudioManager audioManager) {
        mScreen = screen;
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
        if (isSoundOn()) {
            float volume = mRandom.nextFloat() * 0.7f + 0.3f;
            float rate = mRandom.nextFloat() * 0.7f + 0.8f;
            mWhistleStream = mSoundPool.play(mWhistleSound, volume, volume, 1, 0, rate);
        }
    }

    public void playKantrop() {
        if (isSoundOn()) {
            float volume = 0.5f;
            mKantropStream = mSoundPool.play(mKantropSound, volume, volume, 1, -1, 1F);
        }
    }

    public void stopKantropSound() {
        mSoundPool.stop(mKantropStream);
    }

    public void playSplash() {
        if (isSoundOn()) {
            float volume = getVolume();
            volume = volume * 0.2f;
            mSplashStream = mSoundPool.play(mSplashSounds[mRandom.nextInt(SPLASH_SOUNDS_COUNT)], volume, volume, 1, 0, 1F);
        }
    }

    public void playHitSound() {
        if (isSoundOn()) {
            float volume = getVolume();
            mHitStream = mSoundPool.play(mHitSounds[mRandom.nextInt(HIT_SOUNDS_COUNT)], volume, volume, 1, 0, 1F);
        }
    }

    public void playKillSound() {
        if (isSoundOn()) {
            float volume = getVolume();
            mKillStream = mSoundPool.play(mKillSounds[mRandom.nextInt(KILL_SOUNDS_COUNT)], volume, volume, 1, 0, 1F);
        }
    }

    public boolean isAlarmPlaying() {
        return mAlarmStream != 0;
    }

    public void playAlarmSound() {
        if (isSoundOn()) {
            float volume = 0.3f;
            mAlarmStream = mSoundPool.play(mAlarmSound, volume, volume, 1, 0, 1);
        }
    }

    public void stopAlarmSound() {
        mSoundPool.stop(mAlarmStream);
        mAlarmStream = 0;
    }

    public boolean isSoundOn() {
        return GameSettings.get().isSoundOn() && mScreen.isResumed();
    }

    private float getVolume() {
        float actualVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume / maxVolume;
    }
}
