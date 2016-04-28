package com.ivygames.morskoiboi.screen.gameplay;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: create presenter for this class
public class FleetView extends View {

    private static final int DEFAULT_MARGIN_BETWEEN_SHIPS = 4;
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

    @NonNull
    private Rect mCarrierBounds = new Rect();
    @NonNull
    private Rect mBattleshipBounds = new Rect();
    @NonNull
    private Rect mDestroyerBounds = new Rect();
    @NonNull
    private Rect mGunboatBounds = new Rect();

    private final Rect mCarrierSrc;
    private final Rect mBattleshipSrc;
    private final Rect mDestroyerSrc;
    private final Rect mGunboatSrc;

    private Map<Integer, Integer> mMyShipsLeft;
    private Map<Integer, Integer> mEnemyShipsLeft;

    private int mUnitHeight;

    private final FleetViewPresenter mPresenter;
    private final FleetViewRenderer mRenderer;

    public FleetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.Status);
        mMarginBetweenShips = attributesArray.getDimensionPixelOffset(R.styleable.Status_verticalMargin, DEFAULT_MARGIN_BETWEEN_SHIPS);
        attributesArray.recycle();

        Resources resources = getResources();

        mAircraftCarrier = Bitmaps.getBitmap(resources, R.drawable.aircraft_carrier);
        mBattleship = Bitmaps.getBitmap(resources, R.drawable.battleship);
        mDestroyer = Bitmaps.getBitmap(resources, R.drawable.frigate);
        mGunboat = Bitmaps.getBitmap(resources, R.drawable.gunboat);

        mPresenter = new FleetViewPresenter();
        mRenderer = new FleetViewRenderer(resources);

        mCarrierSrc = createRectForBitmap(mAircraftCarrier);
        mBattleshipSrc = createRectForBitmap(mBattleship);
        mDestroyerSrc = createRectForBitmap(mDestroyer);
        mGunboatSrc = createRectForBitmap(mGunboat);
    }

    @NonNull
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

        int w = getWidth();

        int top = mUnitHeight + mMarginBetweenShips;
        mRenderer.drawShip(canvas, mAircraftCarrier, mCarrierSrc, mCarrierBounds, w, mUnitHeight);
        int numOfMyShips = mMyShipsLeft.get(4);
        int numOfEnemyShips = mEnemyShipsLeft.get(4);
        mRenderer.drawLine(canvas, numOfMyShips, numOfEnemyShips, w, mUnitHeight);

        canvas.translate(0, top);
        mRenderer.drawShip(canvas, mBattleship, mBattleshipSrc, mBattleshipBounds, w, mUnitHeight);
        numOfMyShips = mMyShipsLeft.get(3);
        numOfEnemyShips = mEnemyShipsLeft.get(3);
        mRenderer.drawLine(canvas, numOfMyShips, numOfEnemyShips, w, mUnitHeight);

        canvas.translate(0, top);
        mRenderer.drawShip(canvas, mDestroyer, mDestroyerSrc, mDestroyerBounds, w, mUnitHeight);
        numOfMyShips = mMyShipsLeft.get(2);
        numOfEnemyShips = mEnemyShipsLeft.get(2);
        mRenderer.drawLine(canvas, numOfMyShips, numOfEnemyShips, w, mUnitHeight);

        canvas.translate(0, top);
        mRenderer.drawShip(canvas, mGunboat, mGunboatSrc, mGunboatBounds, w, mUnitHeight);
        numOfMyShips = mMyShipsLeft.get(1);
        numOfEnemyShips = mEnemyShipsLeft.get(1);
        mRenderer.drawLine(canvas, numOfMyShips, numOfEnemyShips, w, mUnitHeight);
    }

    /**
     * {@link #init(int[])} must be called before this methid
     */
    public void setMyShips(Collection<Ship> ships) {
        putShipsToMap(ships, mMyShipsLeft);
        invalidate();
    }

    public void setEnemyShips(Collection<Ship> ships) {
        putShipsToMap(ships, mEnemyShipsLeft);
        invalidate();
    }

    private static void putShipsToMap(Collection<Ship> ships, Map<Integer, Integer> map) {
        for (Integer size : map.keySet()) {
            map.put(size, 0);
        }
        for (Ship ship : ships) {
            map.put(ship.getSize(), map.get(ship.getSize()) + 1);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Ln.v("w=" + w + ", h=" + h);

        int textMargin = w / 20;
        int shipArea = (int) (w - mRenderer.getLetterWidth() * 2 - textMargin * 2);
        int unitWidth = shipArea / CARRIER_VISUAL_LENGTH;

        if (unitWidth < 1) {
            Ln.e("fleet: impossible unit size=" + unitWidth + "; w=" + w + "; text_width=" + mRenderer.getLetterWidth());
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

    private static Rect scaleShip(Bitmap bitmap, int destWidth) {
        return scaleShip(bitmap.getHeight(), bitmap.getWidth(), destWidth);
    }

    private static Rect scaleShip(int bitmapHeight, int bitmapWidth, int destWidth) {
        int destHeight = calcDestHeight(bitmapHeight, bitmapWidth, destWidth);
        return new Rect(0, 0, destWidth, destHeight);
    }

    private static int calcDestHeight(Bitmap bitmap, int destWidth) {
        return calcDestHeight(bitmap.getHeight(), bitmap.getWidth(), destWidth);
    }

    private static int calcDestHeight(int bitmapHeight, int bitmapWidth, int destWidth) {
        return (destWidth * bitmapHeight) / bitmapWidth;
    }

    public void init(int[] shipsSizes) {
        mMyShipsLeft = new HashMap<>();
        for (int shipSize : shipsSizes) {
            mMyShipsLeft.put(shipSize, 0);
        }

        mEnemyShipsLeft = new HashMap<>();
        for (int shipSize : shipsSizes) {
            mEnemyShipsLeft.put(shipSize, 0);
        }
    }
}
