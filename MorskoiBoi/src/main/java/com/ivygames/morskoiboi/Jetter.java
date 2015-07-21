package com.ivygames.morskoiboi;

import android.media.JetPlayer;

public class Jetter {

    public void play() {
        JetPlayer jetPlayer = JetPlayer.getJetPlayer();
        jetPlayer.loadJetFile("/sdcard/level1.jet");
        byte segmentId = 0;
// queue segment 5, repeat once, use General MIDI, transpose by -1 octave
        jetPlayer.queueJetSegment(5, -1, 1, -1, 0, segmentId++);
        jetPlayer.play();
    }

}
