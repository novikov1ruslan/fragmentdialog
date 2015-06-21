package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.utils.UiUtils;

import org.commons.logger.Ln;

import java.util.Collection;

public class FleetView extends View {

	private static final int DEFAULT_MARGIN_BETWEEN_SHIPS = 4;

	private final int mMarginBetweenShips;

	private final Bitmap mAircraftCarrier;
	private final Bitmap mBattleship;
	private final Bitmap mDestroyer;
	private final Bitmap mGunboat;

	private Rect mCarrierBounds;
	private Rect mBattleshipBounds;
	private Rect mDestroyerBounds;
	private Rect mGunboatBounds;

	private final Rect mCarrierSrc;
	private final Rect mBattleshipSrc;
	private final Rect mDestroyerSrc;
	private final Rect mGunboatSrc;

	private final Paint mLinePaint;
	private final Paint mTextPaint = new Paint();
	private final Rect mTextBounds = new Rect();
	// private final Rect mOuterRect = new Rect();

	private int[] mMyBuckets;
	private int[] mEnemyBuckets;

	private final int mTextColor;
	private final int mZeroTextColor;

	private int mUnitHeight;

	public FleetView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray attributesArray = getContext().obtainStyledAttributes(attrs, R.styleable.Status);
		mMarginBetweenShips = attributesArray.getDimensionPixelOffset(R.styleable.Status_verticalMargin, DEFAULT_MARGIN_BETWEEN_SHIPS);
		attributesArray.recycle();

		mLinePaint = UiUtils.newStrokePaint(getResources(), R.color.line);
		float mTextSize = context.getResources().getDimension(R.dimen.status_text_size);
		mTextPaint.setTextSize(mTextSize);
		Typeface typeface = Typeface.DEFAULT_BOLD;
		mTextPaint.setTypeface(typeface);
		mTextPaint.setTextAlign(Paint.Align.LEFT);
		mTextPaint.getTextBounds("4", 0, 1, mTextBounds);

		mTextColor = context.getResources().getColor(R.color.status_text);
		mZeroTextColor = context.getResources().getColor(R.color.status_zero_text);

		if (isInEditMode()) {
			mMyBuckets = new int[] { 4, 3, 2, 1 };
			mEnemyBuckets = new int[] { 4, 3, 2, 1 };
		} else {
			mMyBuckets = new int[4];
			mEnemyBuckets = new int[4];
		}

		mAircraftCarrier = Bitmaps.getInstance().getBitmap(R.drawable.aircraft_carrier);
		mBattleship = Bitmaps.getInstance().getBitmap(R.drawable.battleship);
		mDestroyer = Bitmaps.getInstance().getBitmap(R.drawable.frigate);
		mGunboat = Bitmaps.getInstance().getBitmap(R.drawable.gunboat);

		mCarrierSrc = createRectForBitmap(mAircraftCarrier);
		mBattleshipSrc = createRectForBitmap(mBattleship);
		mDestroyerSrc = createRectForBitmap(mDestroyer);
		mGunboatSrc = createRectForBitmap(mGunboat);

		mCarrierBounds = new Rect();
		mBattleshipBounds = new Rect();
		mDestroyerBounds = new Rect();
		mGunboatBounds = new Rect();
	}

	private Rect createRectForBitmap(Bitmap bitmap) {
		return new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mUnitHeight == 0) {
			// this happens if onSizeChanged() could not return good dimensions
			return;
		}

		int top = 0;
		drawShip(mAircraftCarrier, mCarrierSrc, mCarrierBounds, top, 3, canvas);

		top += mUnitHeight + mMarginBetweenShips;
		drawShip(mBattleship, mBattleshipSrc, mBattleshipBounds, top, 2, canvas);

		top += mUnitHeight + mMarginBetweenShips;
		drawShip(mDestroyer, mDestroyerSrc, mDestroyerBounds, top, 1, canvas);

		top += mUnitHeight + mMarginBetweenShips;
		drawShip(mGunboat, mGunboatSrc, mGunboatBounds, top, 0, canvas);
	}

	private void drawShip(Bitmap bitmap, Rect src, Rect dst, int top, int bucket, Canvas canvas) {
		int w = getWidth();

		canvas.save();
		canvas.translate((w - dst.width()) / 2, top + mUnitHeight - dst.height());
		canvas.drawBitmap(bitmap, src, dst, null);
		canvas.restore();

		mTextPaint.setColor(getTextColor(mMyBuckets[bucket]));
		int textLeft = 0;
		canvas.drawText(String.valueOf(mMyBuckets[bucket]), textLeft, top + mUnitHeight, mTextPaint);

		mTextPaint.setColor(getTextColor(mEnemyBuckets[bucket]));
		int textRight = w - mTextBounds.width();
		canvas.drawText(String.valueOf(mEnemyBuckets[bucket]), textRight, top + mUnitHeight, mTextPaint);

		canvas.drawLine(0, top, w, top, mLinePaint);
		int bottom = top + mUnitHeight;
		canvas.drawLine(0, bottom, w, bottom, mLinePaint);
	}

	private int getTextColor(int numOfShips) {
		if (numOfShips == 0) {
			return mZeroTextColor;
		}

		return mTextColor;
	}

	public void setMyShips(Collection<Ship> ships) {

		mMyBuckets = new int[4];
		for (Ship ship : ships) {
			mMyBuckets[ship.getSize() - 1]++;
		}

		invalidate();
	}

	public void setEnemyShips(Collection<Ship> ships) {

		mEnemyBuckets = new int[4];
		for (Ship ship : ships) {
			mEnemyBuckets[ship.getSize() - 1]++;
		}

		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Ln.v("w=" + w + ", h=" + h);

		int textMargin = w / 20;
		int shipArea = w - mTextBounds.width() * 2 - textMargin * 2;
		int unitWidth = shipArea / 4;

		if (unitWidth < 1) {
			Ln.e("impossible unit size=" + unitWidth + "; w=" + w + "; text_width=" + mTextBounds.width());
			// FIXME: maybe because you haven't implemented onMeasure() system thinks no size is needed
			return;
		}

		int allMarginsBetweenShips = mMarginBetweenShips * 3;
		int highestShip = calcDestHeight(mAircraftCarrier, unitWidth * 4);
		int desiredHeight = highestShip * 4 + allMarginsBetweenShips;
		while (desiredHeight > h) {
			unitWidth--;
			highestShip = calcDestHeight(mAircraftCarrier, unitWidth * 4);
			desiredHeight = highestShip * 4 + allMarginsBetweenShips;
		}

		mCarrierBounds = scaleShip(mAircraftCarrier, unitWidth * 4);
		mBattleshipBounds = scaleShip(mBattleship, unitWidth * 3);
		mDestroyerBounds = scaleShip(mDestroyer, unitWidth * 2);
		mGunboatBounds = scaleShip(mGunboat, unitWidth);

		mUnitHeight = mCarrierBounds.height();
	}

	private Rect scaleShip(Bitmap srcBitmap, int destWidth) {
		int destHeight = calcDestHeight(srcBitmap, destWidth);
		return new Rect(0, 0, destWidth, destHeight);
	}

	private int calcDestHeight(Bitmap srcBitmap, int destWidth) {
		return (destWidth * srcBitmap.getHeight()) / srcBitmap.getWidth();
	}
}
