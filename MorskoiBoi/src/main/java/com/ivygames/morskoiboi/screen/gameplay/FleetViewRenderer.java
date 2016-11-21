package com.ivygames.morskoiboi.screen.gameplay;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.GraphicsUtils;
import com.ivygames.morskoiboi.R;

import java.util.Map;

class FleetViewRenderer {
    private static final int BATTLESHIP_LENGTH = 3;
    private static final int DESTROYER_LENGTH = 2;
    private static final int GUNBOAT_LENGTH = 1;

    private static final String WIDEST_LETTER = "4";

    private final Paint mLinePaint;
    private final Paint mTextPaint = new Paint();

    private final int mTextColor;
    private final int mZeroTextColor;
    private final float mLetterWidth;
    private int mUnitHeight;

    private final Bitmap mAircraftCarrier;
    private final Bitmap mBattleship;
    private final Bitmap mDestroyer;
    private final Bitmap mGunboat;

    private final Rect mCarrierSrc;
    private final Rect mBattleshipSrc;
    private final Rect mDestroyerSrc;
    private final Rect mGunboatSrc;

    @NonNull
    private Rect mCarrierBounds = new Rect();
    @NonNull
    private Rect mBattleshipBounds = new Rect();
    @NonNull
    private Rect mDestroyerBounds = new Rect();
    @NonNull
    private Rect mGunboatBounds = new Rect();

    public FleetViewRenderer(Resources resources) {
        mLinePaint = GraphicsUtils.newStrokePaint(resources, R.color.line);
        float textSize = resources.getDimension(R.dimen.status_text_size);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mLetterWidth = mTextPaint.measureText(WIDEST_LETTER);

        mTextColor = resources.getColor(R.color.status_text);
        mZeroTextColor = resources.getColor(R.color.status_zero_text);

        mAircraftCarrier = Bitmaps.getBitmap(resources, R.drawable.aircraft_carrier);
        mBattleship = Bitmaps.getBitmap(resources, R.drawable.battleship);
        mDestroyer = Bitmaps.getBitmap(resources, R.drawable.frigate);
        mGunboat = Bitmaps.getBitmap(resources, R.drawable.gunboat);

        mCarrierSrc = createRectForBitmap(mAircraftCarrier);
        mBattleshipSrc = createRectForBitmap(mBattleship);
        mDestroyerSrc = createRectForBitmap(mDestroyer);
        mGunboatSrc = createRectForBitmap(mGunboat);
    }

    private void drawLine(Canvas canvas, int numOfMyShips, int numOfEnemyShips, int w) {
        if (mUnitHeight == 0) {
            // this happens if onSizeChanged() could not return good dimensions
            return;
        }

        mTextPaint.setColor(getTextColor(numOfMyShips));
        int textLeft = 0;
        canvas.drawText(String.valueOf(numOfMyShips), textLeft, mUnitHeight, mTextPaint);

        mTextPaint.setColor(getTextColor(numOfEnemyShips));
        int textRight = (int) (w - mLetterWidth);
        canvas.drawText(String.valueOf(numOfEnemyShips), textRight, mUnitHeight, mTextPaint);

        canvas.drawLine(0, 0, w, 0, mLinePaint);
        canvas.drawLine(0, mUnitHeight, w, mUnitHeight, mLinePaint);
    }

    private void drawShip(Canvas canvas, Bitmap bitmap, Rect src, Rect dst, int w) {
        if (mUnitHeight == 0) {
            // this happens if onSizeChanged() could not return good dimensions
            return;
        }
        canvas.save();
        canvas.translate((w - dst.width()) / 2, mUnitHeight - dst.height());
        canvas.drawBitmap(bitmap, src, dst, null);
        canvas.restore();
    }

    public float getLetterWidth() {
        return mLetterWidth;
    }

    private int getTextColor(int numOfShips) {
        if (numOfShips == 0) {
            return mZeroTextColor;
        }

        return mTextColor;
    }

    public void setUnitHeight(int unitWidth) {
        mCarrierBounds = scaleShip(mAircraftCarrier, unitWidth * FleetView.CARRIER_LENGTH);
        mBattleshipBounds = scaleShip(mBattleship, unitWidth * BATTLESHIP_LENGTH);
        mDestroyerBounds = scaleShip(mDestroyer, unitWidth * DESTROYER_LENGTH);
        mGunboatBounds = scaleShip(mGunboat, unitWidth * GUNBOAT_LENGTH);
        mUnitHeight = mCarrierBounds.height();
    }

    @NonNull
    private Rect createRectForBitmap(Bitmap bitmap) {
        return new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    private void drawAircraftCarrier(Canvas canvas, int w) {
        drawShip(canvas, mAircraftCarrier, mCarrierSrc, mCarrierBounds, w);
    }

    private void drawBattleship(Canvas canvas, int w) {
        drawShip(canvas, mBattleship, mBattleshipSrc, mBattleshipBounds, w);
    }

    private void drawDestroyer(Canvas canvas, int w) {
        drawShip(canvas, mDestroyer, mDestroyerSrc, mDestroyerBounds, w);
    }

    private void drawGunboat(Canvas canvas, int w) {
        drawShip(canvas, mGunboat, mGunboatSrc, mGunboatBounds, w);
    }

    public void drawAll(Canvas canvas, int w, int marginBetweenShips, Map<Integer, Integer> myShipsLeft,
                        Map<Integer, Integer> enemyShipsLeft) {

        int top = marginBetweenShips + mUnitHeight;

        drawAircraftCarrier(canvas, w);
        int numOfMyShips = myShipsLeft.get(FleetView.CARRIER_LENGTH);
        int numOfEnemyShips = enemyShipsLeft.get(FleetView.CARRIER_LENGTH);
        drawLine(canvas, numOfMyShips, numOfEnemyShips, w);

        canvas.translate(0, top);
        drawBattleship(canvas, w);
        numOfMyShips = myShipsLeft.get(BATTLESHIP_LENGTH);
        numOfEnemyShips = enemyShipsLeft.get(BATTLESHIP_LENGTH);
        drawLine(canvas, numOfMyShips, numOfEnemyShips, w);

        canvas.translate(0, top);
        drawDestroyer(canvas, w);
        numOfMyShips = myShipsLeft.get(DESTROYER_LENGTH);
        numOfEnemyShips = enemyShipsLeft.get(DESTROYER_LENGTH);
        drawLine(canvas, numOfMyShips, numOfEnemyShips, w);

        canvas.translate(0, top);
        drawGunboat(canvas, w);
        numOfMyShips = myShipsLeft.get(GUNBOAT_LENGTH);
        numOfEnemyShips = enemyShipsLeft.get(GUNBOAT_LENGTH);
        drawLine(canvas, numOfMyShips, numOfEnemyShips, w);
    }

    private static Rect scaleShip(Bitmap bitmap, int destWidth) {
        return scaleShip(bitmap.getHeight(), bitmap.getWidth(), destWidth);
    }

    private static Rect scaleShip(int bitmapHeight, int bitmapWidth, int destWidth) {
        int destHeight = FleetView.calcDestHeight(bitmapHeight, bitmapWidth, destWidth);
        return new Rect(0, 0, destWidth, destHeight);
    }
}
