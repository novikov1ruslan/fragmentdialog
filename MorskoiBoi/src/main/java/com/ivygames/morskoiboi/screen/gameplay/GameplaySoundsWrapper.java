package com.ivygames.morskoiboi.screen.gameplay;

import android.media.AudioManager;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.screen.BattleshipScreen;

class GameplaySoundsWrapper extends GameplaySounds {

    @NonNull
    private final BattleshipScreen mScreen;

    @NonNull
    private final GameSettings mSettings;

    GameplaySoundsWrapper(@NonNull AudioManager am,
                          @NonNull BattleshipScreen screen,
                          @NonNull GameSettings settings) {
        super(am);
        mScreen = screen;
        mSettings = settings;
    }

    @Override
    public void playWhistleSound() {
        if (isSoundOn()) {
            super.playWhistleSound();
        }
    }

    @Override
    public void playKantrop() {
        if (isSoundOn()) {
            super.playKantrop();
        }
    }

    @Override
    public void playSplash() {
        if (isSoundOn()) {
            super.playSplash();
        }
    }

    @Override
    public void playHitSound() {
        if (isSoundOn()) {
            super.playHitSound();
        }
    }

    @Override
    public void playKillSound() {
        if (isSoundOn()) {
            super.playKillSound();
        }
    }

    @Override
    public void playAlarmSound() {
        if (isSoundOn()) {
            super.playAlarmSound();
        }
    }

    private boolean isSoundOn() {
        return mSettings.isSoundOn() && mScreen.isResumed();
    }
}
