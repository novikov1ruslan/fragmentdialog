package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.Button;

import com.ivygames.morskoiboi.R;

public class InvitationButton extends Button {

	private final Bitmap mBitmap;
	private int mLeft;
	private int mTop;
	private Paint mPaint;
	private boolean mShowInvitation;

	public InvitationButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBitmap = createInvitationBitmap();
	}

	public InvitationButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mBitmap = createInvitationBitmap();
	}

	private Bitmap createInvitationBitmap() {
		return BitmapFactory.decodeResource(getResources(), R.drawable.invitation);
	}

	public void showInvitation() {
		mShowInvitation = true;
		invalidate();
	}

	public void hideInvitation() {
		mShowInvitation = false;
		invalidate();
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		if (mShowInvitation) {
			canvas.drawBitmap(mBitmap, mLeft, mTop, mPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mLeft = w - mBitmap.getWidth() - getPaddingRight();
		mTop = (h - mBitmap.getHeight()) / 2;
	}
}
