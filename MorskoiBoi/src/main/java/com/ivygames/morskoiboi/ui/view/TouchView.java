package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.commons.logger.Ln;

/*package*/abstract class TouchView extends View {

	protected int mTouchX;
	protected int mTouchY;
	protected int mTouchAction;
	protected int mDragStatus;

	protected static final int START_DRAGGING = 1;
	private static final int STOP_DRAGGING = 0;

	public TouchView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mTouchAction = MotionEvent.ACTION_UP;
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		mTouchX = (int) event.getX();
		mTouchY = (int) event.getY();
		mTouchAction = event.getAction();
		if (mTouchAction == MotionEvent.ACTION_DOWN) {
			Ln.v("ACTION_DOWN: " + mTouchX + ":" + mTouchY);
			mDragStatus = START_DRAGGING;
		} else if (mTouchAction == MotionEvent.ACTION_UP) {
			Ln.v("ACTION_UP: " + mTouchX + ":" + mTouchY);
			mDragStatus = STOP_DRAGGING;
		}

		return true;
	}

	protected int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// return specSize;

		int desiredWidth = specSize;// mDisplayWidth;// +
									// getPaddingLeft()
									// +
									// getPaddingRight();
		if (specMode == MeasureSpec.EXACTLY) {
			// Ln.v("MeasureSpec.EXACTLY");
			// We were told how big to be
			return specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// return desiredWidth < specSize ? desiredWidth : specSize;
			// Ln.v("MeasureSpec.AT_MOST");
			return specSize;
		} else {
			// Ln.v("MeasureSpec.UNSPECIFIED");
			return desiredWidth;
		}
	}

	protected int measureHeight(int measureSpec) {
		// int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		return specSize;

		// int desiredHeight = mDisplayHeight;// +
		// // getPaddingTop()
		// // +
		// // getPaddingBottom();
		// if (specMode == MeasureSpec.EXACTLY) {
		// // We were told how big to be
		// return specSize;
		// } else if (specMode == MeasureSpec.AT_MOST) {
		// return desiredHeight < specSize ? desiredHeight : specSize;
		// } else {
		// return desiredHeight;
		// }
	}

}
