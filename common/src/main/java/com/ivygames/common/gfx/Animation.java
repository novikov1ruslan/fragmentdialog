package com.ivygames.common.gfx;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    @NonNull
    private final List<Bitmap> mBitmaps = new ArrayList<>();
    @NonNull
    private final Rect mBounds = new Rect();

    private final int mDuration;

    private int mFrameDuration;
    private long mStartTime;

    public Animation(int frameDuration) {
        mDuration = frameDuration;
    }

    public void start() {
        mStartTime = SystemClock.elapsedRealtime();
        mFrameDuration = mDuration / mBitmaps.size();
    }

    public boolean isRunning() {
        return mStartTime + mFrameDuration * mBitmaps.size() > SystemClock.elapsedRealtime();
    }

    public void adFrame(@NonNull Bitmap bitmap) {
        mBounds.right = bitmap.getWidth();
        mBounds.bottom = bitmap.getHeight();
        mBitmaps.add(bitmap);
    }

    @NonNull
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

    @NonNull
    public Rect getBounds() {
        return mBounds;
    }

    public long getFrameDuration() {
        return mFrameDuration;
    }
}
