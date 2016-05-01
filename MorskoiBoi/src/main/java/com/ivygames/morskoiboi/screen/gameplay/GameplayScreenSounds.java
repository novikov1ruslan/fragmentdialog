package com.ivygames.morskoiboi.screen.gameplay;

import android.media.AudioManager;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.screen.BattleshipScreen;

public class GameplayScreenSounds extends GameplaySoundManager {

    @NonNull
    private final BattleshipScreen mScreen;

    @NonNull
    private final GameSettings mSettings;

    public GameplayScreenSounds(@NonNull AudioManager audioManager, @NonNull BattleshipScreen screen, @NonNull GameSettings settings) {
        super(audioManager);
        mScreen = screen;
        mSettings = settings;
    }

    public void playWhistleSound() {
        if (isSoundOn()) {
            super.playWhistleSound();
        }
    }

    public void playKantrop() {
        if (isSoundOn()) {
            super.playKantrop();
        }
    }

    public void playSplash() {
        if (isSoundOn()) {
            super.playSplash();
        }
    }

    public void playHitSound() {
        if (isSoundOn()) {
            super.playHitSound();
        }
    }

    public void playKillSound() {
        if (isSoundOn()) {
            super.playKillSound();
        }
    }

    public void playAlarmSound() {
        if (isSoundOn()) {
            super.playAlarmSound();
        }
    }

    private boolean isSoundOn() {
        return mSettings.isSoundOn() && mScreen.isResumed();
    }
}
