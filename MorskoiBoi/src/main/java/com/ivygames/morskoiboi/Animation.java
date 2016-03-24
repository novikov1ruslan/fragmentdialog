package com.ivygames.morskoiboi;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    private final List<Bitmap> mBitmaps;
    private int mFrameDuration;
    private long mStartTime;
    private final Rect mBounds;
    private final int mDuration;

    public Animation(int frameDuration) {
        mDuration = frameDuration;
        mBitmaps = new ArrayList<>();
        mBounds = new Rect();
    }

    public void start() {
        mStartTime = SystemClock.elapsedRealtime();
        mFrameDuration = mDuration / mBitmaps.size();
    }

    public boolean isRunning() {
        return mStartTime + mFrameDuration * mBitmaps.size() > SystemClock.elapsedRealtime();
    }

    public void adFrame(Bitmap bitmap) {
        mBounds.right = bitmap.getWidth();
        mBounds.bottom = bitmap.getHeight();
        mBitmaps.add(bitmap);
    }

    public Bitmap getCurrentFrame() {
        long offset = SystemClock.elapsedRealtime() - mStartTime;
        int index = (int) (offset / mFrameDuration);

        index = normalizeIndex(index);

        return mBitmaps.get(index);
    }

    private int normalizeIndex(int index) {
        if (index < 0) {
            index = 0;
        }

        if (index >= mBitmaps.size()) {
            index = mBitmaps.size() - 1;
        }
        return index;
    }

    public Rect getBounds() {
        return mBounds;
    }

    public long getFrameDuration() {
        return mFrameDuration;
    }
}
