package com.ivygames.morskoiboi.ui.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

public class SetupBoardPresenter extends BasePresenter {
    private final Rect mShipSelectionRect = new Rect();
    private final Rect mShipDisplayRect = new Rect();
    private Point shipDisplayCenter = new Point();
    private final Rect mPickedShipRect = new Rect();

    public SetupBoardPresenter(int boardSize, float dimension) {
        super(boardSize, dimension);
    }

    public void measure(int w, int h, int hPadding, int vPadding) {
        // calculate mShipSelectionRect (it starts from left=0, top=0)
        mShipSelectionRect.right = w / 2;
        mShipSelectionRect.bottom = h / 4;

        // calculate mShipDisplayRect (it starts from top=0)
        mShipDisplayRect.left = mShipSelectionRect.right + 1;
        mShipDisplayRect.right = w;
        mShipDisplayRect.bottom = mShipSelectionRect.bottom;

        h = h - mShipSelectionRect.height();
        super.measure(w, h, 0, mShipDisplayRect.height(), hPadding, vPadding);
    }

    public int getShipWidthInPx(int shipSize) {
        return shipSize * mCellSizePx;
    }

    public boolean isInShipSelectionArea(int x, int y) {
        return mShipSelectionRect.contains(x, y);
    }

    @NonNull
    public Point getTopLeftPointInTopArea(int shipSize) {
        int left = mShipSelectionRect.centerX() - getShipWidthInPx(shipSize) / 2;
        int top = mShipSelectionRect.centerY() - mCellSizePx / 2;
        return new Point(left, top);
    }

    public Point getShipDisplayAreaCenter() {
        shipDisplayCenter.set(mShipDisplayRect.centerX(), mShipDisplayRect.centerY());
        return shipDisplayCenter;
    }

    public Vector2 getAim(Rect mPickedShipRect) {
        int shipInBoardCoordinatesX = mPickedShipRect.left - mBoardRect.left + mHalfCellSize;
        int shipInBoardCoordinatesY = mPickedShipRect.top - mBoardRect.top + mHalfCellSize;
        int i = shipInBoardCoordinatesX / mCellSizePx;
        int j = shipInBoardCoordinatesY / mCellSizePx;
        return Vector2.get(i, j);
    }

    public Rect centerPickedShipAround(int touchX, int touchY, Ship mPickedShip) {
        int widthInPx = getShipWidthInPx(mPickedShip.getSize());
        int halfWidthInPx = getShipWidthInPx(mPickedShip.getSize()) / 2;
        boolean isHorizontal = mPickedShip.isHorizontal();
        mPickedShipRect.left = touchX - (isHorizontal ? halfWidthInPx : mHalfCellSize);
        mPickedShipRect.top = touchY - (isHorizontal ? mHalfCellSize : halfWidthInPx);
        mPickedShipRect.right = mPickedShipRect.left + (isHorizontal ? widthInPx : mCellSizePx);
        mPickedShipRect.bottom = mPickedShipRect.top + (isHorizontal ? mCellSizePx : widthInPx);

        return mPickedShipRect;
    }
}
