package com.ivygames.morskoiboi;

public class Progress {

    public final int progress;

    public Progress(int progress) {
        this.progress = progress;
    }

    // TODO: remove
    public int getScores() {
        return progress;
    }

    @Override
    public String toString() {
        return "[progress=" + progress + "]";
    }
}
