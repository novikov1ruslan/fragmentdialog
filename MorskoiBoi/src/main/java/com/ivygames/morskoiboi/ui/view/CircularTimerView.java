package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.utils.UiUtils;

public class CircularTimerView extends View {

	private static final int DEFAULT_TOTAL_TIME_SECONDS = 120;

	private int mTotalTime = DEFAULT_TOTAL_TIME_SECONDS;
	private float mTime = DEFAULT_TOTAL_TIME_SECONDS;
	private final Paint mInnerPaint;
	private final Paint mInnerWarningPaint;
	private final Paint mOuterPaint;

	private int mAlarmTimeSeconds;

	public CircularTimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mOuterPaint = UiUtils.newStrokePaint(getResources(), R.color.main);
		// mInnerPaint = UiUtils.newFillPaint(getResources(), R.color.miss_background);
		mInnerPaint = UiUtils.newFillPaint(getResources(), R.color.main);
		mInnerWarningPaint = UiUtils.newFillPaint(getResources(), R.color.hit);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// int height = getHeight();
		// mInnerRect.top = height - valueLeft(height);
		// canvas.drawRect(mInnerRect, getInnerPaint());
		// canvas.drawRect(mOuterRect, mOuterPaint);

		int width = getWidth();
		int height = getHeight();
		int diameter = width > height ? width : height;
		canvas.drawCircle(width / 2, height / 2, diameter / 2, mOuterPaint);
	}

	private int valueLeft(int height) {
		return (int) (height * mTime) / mTotalTime;
	}

	private Paint getInnerPaint() {
		return mTime > mAlarmTimeSeconds ? mInnerPaint : mInnerWarningPaint;
	}

	public void setTotalTime(int time) {
		mTotalTime = time;
	}

	public void setTime(float time) {
		mTime = time;
		invalidate();
	}

	// @Override
	// protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// super.onSizeChanged(w, h, oldw, oldh);
	//
	// int width = getWidth();
	// int height = getHeight();
	// mOuterRect.right = width - 1;
	// mOuterRect.bottom = height - 1;
	//
	// mInnerRect.right = mOuterRect.right;
	// mInnerRect.bottom = mOuterRect.bottom;
	// }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setAlarmTime(int alarmTimeSeconds) {
		mAlarmTimeSeconds = alarmTimeSeconds;
	}
}
