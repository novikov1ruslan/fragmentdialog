package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ivygames.morskoiboi.R;

public class NotepadFrameLayout extends FrameLayout {

	private final Paint mLinePaint;
	private final int mCellSize;

	public NotepadFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		Resources resources = getResources();

		mLinePaint = new Paint();
		mLinePaint.setColor(resources.getColor(R.color.line2));
		mLinePaint.setStrokeWidth(resources.getDimension(R.dimen.layout_line_thickness));
		mLinePaint.setStyle(Paint.Style.STROKE);

		mCellSize = resources.getDimensionPixelSize(R.dimen.background_cell);

		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// draw vertical lines
		for (int i = 0; i < getWidth(); i += mCellSize) {
			canvas.drawLine(i, 0, i, getHeight(), mLinePaint);
		}

		// draw horizontal lines
		for (int i = 0; i < getHeight(); i += mCellSize) {
			canvas.drawLine(0, i, getWidth(), i, mLinePaint);
		}
	}
}
