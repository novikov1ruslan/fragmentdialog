package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.utils.UiUtils;

public class TimerView extends View {

	private static final int DEFAULT_TOTAL_TIME = 120000;

	private int mTotalTime = DEFAULT_TOTAL_TIME;
	private int mTime = DEFAULT_TOTAL_TIME;
	private final Rect mOuterRect = new Rect();
	private final Rect mInnerRect = new Rect();
	private final Paint mInnerPaint;
	private final Paint mInnerWarningPaint;
	private final Paint mOuterPaint;

	private int mAlarmTimeSeconds;

	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mOuterPaint = UiUtils.newStrokePaint(getResources(), R.color.main);
		// mInnerPaint = UiUtils.newFillPaint(getResources(), R.color.miss_background);
		mInnerPaint = UiUtils.newFillPaint(getResources(), R.color.main);
		mInnerWarningPaint = UiUtils.newFillPaint(getResources(), R.color.hit);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight();
		mInnerRect.top = height - (height * mTime) / mTotalTime;

		canvas.drawRect(mInnerRect, getInnerPaint());
		canvas.drawRect(mOuterRect, mOuterPaint);
	}

	private Paint getInnerPaint() {
		return mTime > mAlarmTimeSeconds ? mInnerPaint : mInnerWarningPaint;
	}

	public void setTotalTime(int time) {
		mTotalTime = time;
	}

	public void setTime(int time) {
		mTime = time;
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		int width = getWidth();
		int height = getHeight();
		mOuterRect.right = width - 1;
		mOuterRect.bottom = height - 1;

		mInnerRect.right = mOuterRect.right;
		mInnerRect.bottom = mOuterRect.bottom;
	}

	public void setAlarmTime(int alarmTimeSeconds) {
		mAlarmTimeSeconds = alarmTimeSeconds;
	}
}
