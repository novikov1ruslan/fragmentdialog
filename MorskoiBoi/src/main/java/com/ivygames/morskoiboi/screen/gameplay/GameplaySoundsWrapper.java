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
