package com.ivygames.common.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;

public class MusicPlayer {
    public static final int NO_SOUND = 0;

    private static MediaPlayer mMediaPlayer;

    public static MusicPlayer create(Context context, @RawRes int musicId) {
        return new MusicPlayer(context, musicId);
    }

    private MusicPlayer(Context context, @RawRes int resId) {
//        try {
//            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);
//            mMediaPlayer = MediaPlayer.create(context, resId);
//            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    Ln.w("media_error: " + what);
//                    return false;
//                }
//            });
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.setVolume(0.5f, 0.5f);
//        } catch (Exception e) {
//            Ln.w("media_exception: " + e.getMessage());
//        }
    }

    public void play(int music) {
//        if (mMediaPlayer == null || !GameSettings.get().isSoundOn()) {
//            return;
//        }
//
//        if (music == NO_SOUND) {
//            stop();
//        } else {
//            mMediaPlayer.start();
//        }
    }

    public void stop() {
//        if (mMediaPlayer == null) {
//            return;
//        }
//
//        if (mMediaPlayer.isPlaying()) {
//            mMediaPlayer.pause();
//            mMediaPlayer.seekTo(0);
//        } else {
//            Ln.w("asked to stop before playing");
//        }
    }

    public void pause() {
//        if (mMediaPlayer == null) {
//            return;
//        }
//
//        mMediaPlayer.pause();
    }

    public void release() {
//        if (mMediaPlayer == null) {
//            return;
//        }
//
//        mMediaPlayer.release();
    }
}
