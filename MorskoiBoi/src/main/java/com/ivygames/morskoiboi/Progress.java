package com.ivygames.morskoiboi;

public class Progress {

    public final int progress;

    public Progress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "[progress=" + progress + "]";
    }
}
