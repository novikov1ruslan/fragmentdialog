package com.ivygames.morskoiboi;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;

import com.ivygames.morskoiboi.model.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Animation {

	private final List<Bitmap> mBitmaps;
	private int mFrameDuration;
	private long mStartTime;
	private Vector2 mAim;
	private final Rect mBounds;
	private final int mDuration;
	private final float mCellRatio;

	public Animation(int frameDuration, float cellRatio) {
		mDuration = frameDuration;
		mCellRatio = cellRatio;
		mBitmaps = new ArrayList<Bitmap>();
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

		// normalize
		if (index < 0) {
			index = 0;
		}

		if (index >= mBitmaps.size()) {
			index = mBitmaps.size() - 1;
		}

		return mBitmaps.get(index);
	}

	public void setAim(Vector2 aim) {
		mAim = aim;
	}

	public Vector2 getAim() {
		return mAim;
	}

	public Rect getBounds() {
		return mBounds;
	}

	public long getFrameDuration() {
		return mFrameDuration;
	}

	public float getCellRatio() {
		return mCellRatio;
	}
}
