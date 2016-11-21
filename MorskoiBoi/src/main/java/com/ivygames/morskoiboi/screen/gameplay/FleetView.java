package com.ivygames.morskoiboi.screen.gameplay;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: create presenter for this class
public class FleetView extends View {

    private static final int DEFAULT_MARGIN_BETWEEN_SHIPS = 4;

    static final int CARRIER_LENGTH = 4;

    private final int mMarginBetweenShips;

    private Map<Integer, Integer> mMyShipsLeft;
    private Map<Integer, Integer> mEnemyShipsLeft;

    private final FleetViewRenderer mRenderer;

    private final int largestShipHeight;
    private final int largestShipWidth;

    public FleetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.Status);
        mMarginBetweenShips = attributesArray.getDimensionPixelOffset(R.styleable.Status_verticalMargin, DEFAULT_MARGIN_BETWEEN_SHIPS);
        attributesArray.recycle();

        Resources resources = getResources();

        Bitmap aircraftCarrier = Bitmaps.getBitmap(resources, R.drawable.aircraft_carrier);
        largestShipHeight = aircraftCarrier.getHeight();
        largestShipWidth = aircraftCarrier.getWidth();

        mRenderer = new FleetViewRenderer(resources);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();

        mRenderer.drawAll(canvas, w, mMarginBetweenShips, mMyShipsLeft, mEnemyShipsLeft);
    }

    /**
     * {@link #init(int[])} must be called before this method
     */
    public void setMyShips(Collection<Ship> ships) {
        putShipsToMap(ships, mMyShipsLeft);
        invalidate();
    }

    /**
     * {@link #init(int[])} must be called before this method
     */
    public void setEnemyShips(Collection<Ship> ships) {
        putShipsToMap(ships, mEnemyShipsLeft);
        invalidate();
    }

    private static void putShipsToMap(Collection<Ship> ships, Map<Integer, Integer> map) {
        for (Integer size : map.keySet()) {
            map.put(size, 0);
        }
        for (Ship ship : ships) {
            map.put(ship.size, map.get(ship.size) + 1);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Ln.v("w=" + w + ", h=" + h);

        int textMargin = w / 20;
        int shipArea = (int) (w - mRenderer.getLetterWidth() * 2 - textMargin * 2);
        int unitWidth = shipArea / CARRIER_LENGTH;

        if (unitWidth < 1) {
            Ln.e("fleet: impossible unit size=" + unitWidth + "; w=" + w + "; h=" + h + ", " + Build.DEVICE);
            // FIXME: maybe because you haven't implemented onMeasure() system thinks no size is needed
            return;
        }

        int typesOfShips = mMyShipsLeft.keySet().size();
        int allMarginsBetweenShips = mMarginBetweenShips * (typesOfShips - 1);
        int highestShip = calcDestHeight(largestShipHeight, largestShipWidth, unitWidth * CARRIER_LENGTH);
        int desiredHeight = highestShip * typesOfShips + allMarginsBetweenShips;
        while (desiredHeight > h) {
            unitWidth--;
            highestShip = calcDestHeight(largestShipHeight, largestShipWidth, unitWidth * CARRIER_LENGTH);
            desiredHeight = highestShip * typesOfShips + allMarginsBetweenShips;
        }

        mRenderer.setUnitHeight(unitWidth);
    }

    static int calcDestHeight(int bitmapHeight, int bitmapWidth, int destWidth) {
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
