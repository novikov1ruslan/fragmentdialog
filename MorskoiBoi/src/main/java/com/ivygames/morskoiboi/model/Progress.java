package com.ivygames.morskoiboi.model;

public class Progress {

    public Progress(int progress) {
        this.progress = progress;
    }

    public final int progress;

    public int getScores() {
        return progress;
    }

    @Override
    public String toString() {
        return "[progress=" + progress + "]";
    }
}
