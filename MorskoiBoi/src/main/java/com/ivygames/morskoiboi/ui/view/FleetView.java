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
import java.util.HashMap;
import java.util.Map;

public class FleetView extends View {

    private static final int DEFAULT_MARGIN_BETWEEN_SHIPS = 4;
    private static final String WIDEST_LETTER = "4";
    private static final int TYPES_OF_SHIPS = 4;

    private static final int CARRIER_VISUAL_LENGTH = 4;
    private static final int BATTLESHIP_VISUAL_LENGTH = 3;
    private static final int DESTROYER_VISUAL_LENGTH = 2;
    private static final int GUNBOAT_VISUAL_LENGTH = 1;

    private final int mMarginBetweenShips;

    private final Bitmap mAircraftCarrier;
    private final Bitmap mBattleship;
    private final Bitmap mDestroyer;
    private final Bitmap mGunboat;
    private final float mLetterWidth;

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

    private final Map<Integer, Integer> mMyBuckets = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> mEnemyBuckets = new HashMap<Integer, Integer>();

    private final int mTextColor;
    private final int mZeroTextColor;

    private int mUnitHeight;

    public FleetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributesArray = getContext().obtainStyledAttributes(attrs, R.styleable.Status);
        mMarginBetweenShips = attributesArray.getDimensionPixelOffset(R.styleable.Status_verticalMargin, DEFAULT_MARGIN_BETWEEN_SHIPS);
        attributesArray.recycle();

        mLinePaint = UiUtils.newStrokePaint(getResources(), R.color.line);
        float mTextSize = getResources().getDimension(R.dimen.status_text_size);
        mTextPaint.setTextSize(mTextSize);
        Typeface typeface = Typeface.DEFAULT_BOLD;
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mLetterWidth = mTextPaint.measureText(WIDEST_LETTER);

        mTextColor = getResources().getColor(R.color.status_text);
        mZeroTextColor = getResources().getColor(R.color.status_zero_text);

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

        mTextPaint.setColor(getTextColor(mMyBuckets.get(bucket)));
        int textLeft = 0;
        canvas.drawText(String.valueOf(mMyBuckets.get(bucket)), textLeft, top + mUnitHeight, mTextPaint);

        mTextPaint.setColor(getTextColor(mEnemyBuckets.get(bucket)));
        int textRight = (int) (w - mLetterWidth);
        canvas.drawText(String.valueOf(mEnemyBuckets.get(bucket)), textRight, top + mUnitHeight, mTextPaint);

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
        updateShipCounts(ships, mMyBuckets);
        invalidate();
    }

    private void updateShipCounts(Collection<Ship> ships, Map<Integer, Integer> map) {
        for (Ship ship : ships) {
            Integer amount = map.get(ship.getSize());
            if (amount == null) {
                amount = 0;
            }

            map.put(ship.getSize(), amount + 1);
        }
    }

    public void setEnemyShips(Collection<Ship> ships) {
        updateShipCounts(ships, mEnemyBuckets);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Ln.v("w=" + w + ", h=" + h);

        int textMargin = w / 20;
        int shipArea = (int) (w - mLetterWidth * 2 - textMargin * 2);
        int unitWidth = shipArea / CARRIER_VISUAL_LENGTH;

        if (unitWidth < 1) {
            Ln.e("impossible unit size=" + unitWidth + "; w=" + w + "; text_width=" + mLetterWidth);
            // FIXME: maybe because you haven't implemented onMeasure() system thinks no size is needed
            return;
        }

        int allMarginsBetweenShips = mMarginBetweenShips * (TYPES_OF_SHIPS - 1);
        int highestShip = calcDestHeight(mAircraftCarrier, unitWidth * CARRIER_VISUAL_LENGTH);
        int desiredHeight = highestShip * TYPES_OF_SHIPS + allMarginsBetweenShips;
        while (desiredHeight > h) {
            unitWidth--;
            highestShip = calcDestHeight(mAircraftCarrier, unitWidth * CARRIER_VISUAL_LENGTH);
            desiredHeight = highestShip * TYPES_OF_SHIPS + allMarginsBetweenShips;
        }

        mCarrierBounds = scaleShip(mAircraftCarrier, unitWidth * CARRIER_VISUAL_LENGTH);
        mBattleshipBounds = scaleShip(mBattleship, unitWidth * BATTLESHIP_VISUAL_LENGTH);
        mDestroyerBounds = scaleShip(mDestroyer, unitWidth * DESTROYER_VISUAL_LENGTH);
        mGunboatBounds = scaleShip(mGunboat, unitWidth * GUNBOAT_VISUAL_LENGTH);

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
