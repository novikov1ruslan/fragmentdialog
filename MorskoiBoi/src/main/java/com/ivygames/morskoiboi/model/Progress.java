package com.ivygames.morskoiboi.model;

public class Progress {

    public Progress(int progress) {
        mProgress = progress;
    }

    private final int mProgress;

    public int getScores() {
        return mProgress;
    }

    @Override
    public String toString() {
        return "[progress=" + mProgress + "]";
    }
}
