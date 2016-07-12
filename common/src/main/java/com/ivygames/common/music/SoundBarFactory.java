package com.ivygames.common.music;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import java.io.IOException;

public class SoundBarFactory {
    public static SoundBar create(@NonNull Context context, @NonNull String soundName) {
        try {
            AssetManager assets = context.getAssets();
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            return new SoundBarImpl(assets.openFd(soundName), audioManager);
        } catch (IOException ioe) {
            Ln.w(ioe);
        }

        return new NullSoundBar();
    }

}
