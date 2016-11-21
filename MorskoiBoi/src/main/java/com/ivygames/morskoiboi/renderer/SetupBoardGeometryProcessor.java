package com.ivygames.morskoiboi.renderer;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Vector2;
import com.ivygames.battleship.ship.Ship;

public final class SetupBoardGeometryProcessor extends BaseGeometryProcessor {
    @NonNull
    private final Rect mShipSelectionRect = new Rect();
    @NonNull
    private final Rect mShipDisplayRect = new Rect();
    @NonNull
    private final Point shipDisplayCenter = new Point();
    @NonNull
    private Rect mPickedShipRect = new Rect();
    @NonNull
    private final Rect mRectForCell = new Rect();

    public SetupBoardGeometryProcessor(int boardSize, float turnBorderSize) {
        super(boardSize, turnBorderSize);
    }

    @Override
    protected void measure(int w, int h, int hPadding, int vPadding) {
        // calculate mShipSelectionRect (it starts from left=0, top=0)
        mShipSelectionRect.right = w / 2;
        mShipSelectionRect.bottom = h / 4;

        // calculate mShipDisplayRect (it starts from top=0)
        mShipDisplayRect.left = mShipSelectionRect.right + 1;
        mShipDisplayRect.right = w;
        mShipDisplayRect.bottom = mShipSelectionRect.bottom;

        shipDisplayCenter.set(mShipDisplayRect.centerX(), mShipDisplayRect.centerY());

        h = h - mShipSelectionRect.height();
        super.measure(w, h, hPadding, vPadding);
        setBoardVerticalOffset(mShipDisplayRect.height());
    }

    private int getShipWidthInPx(int shipSize) {
        return shipSize * mCellSizePx;
    }

    final boolean isInDockArea(@NonNull Point p) {
        return isInDockArea(p.x, p.y);
    }

    final boolean isInDockArea(int x, int y) {
        return mShipSelectionRect.contains(x, y);
    }

    @NonNull
    final Point getShipDisplayAreaCenter() {
        return shipDisplayCenter;
    }

    @NonNull
    final Rect getRectForCell(int i, int j) {
        int left = mBoardRect.left + i * mCellSizePx + 1;
        int top = mBoardRect.top + j * mCellSizePx + 1;
        int right = left + mCellSizePx;
        int bottom = top + mCellSizePx;

        mRectForCell.left = left + 1;
        mRectForCell.top = top + 1;
        mRectForCell.right = right;
        mRectForCell.bottom = bottom;

        return mRectForCell;
    }

    @NonNull
    final Rect getRectForDockedShip(@NonNull Ship ship) {
        Point p = getTopLeftPointInTopArea(ship.size);
        return getRectForShip(ship, p);
    }

    @NonNull
    private Point getTopLeftPointInTopArea(int shipSize) {
        int left = mShipSelectionRect.centerX() - getShipWidthInPx(shipSize) / 2;
        int top = mShipSelectionRect.centerY() - mCellSizePx / 2;
        return new Point(left, top);
    }

    @NonNull
    final Rect getPickedShipRect(@NonNull Ship ship, @NonNull Point p) {
        return getPickedShipRect(ship, p.x, p.y);
    }

    @NonNull
    final Rect getPickedShipRect(@NonNull Ship ship, int x, int y) {
        mPickedShipRect = centerPickedShipRectAround(ship, x, y);
        return mPickedShipRect;
    }

    @NonNull
    final AimingG getAimingForShip(@NonNull Ship ship, int i, int j) {
        int width = ship.isHorizontal() ? ship.size : 1;
        int height = ship.isHorizontal() ? 1 : ship.size;
        return getAimingG(i, j, width, height);
    }

    @NonNull
    final Vector2 getPickedShipCoordinate(@NonNull Ship ship, @NonNull Point p) {
        return getPickedShipCoordinate(ship, p.x, p.y);
    }

    @NonNull
    final Vector2 getPickedShipCoordinate(@NonNull Ship ship, int x, int y) {
        mPickedShipRect = centerPickedShipRectAround(ship, x, y);
        return getPickedShipCoordinate(mPickedShipRect);
    }

    @NonNull
    private Rect centerPickedShipRectAround(@NonNull Ship ship, int x, int y) {
        int widthInPx = getShipWidthInPx(ship.size);
        int halfWidthInPx = widthInPx / 2;
        boolean isHorizontal = ship.isHorizontal();
        mPickedShipRect.left = x - (isHorizontal ? halfWidthInPx : mHalfCellSize);
        mPickedShipRect.top = y - (isHorizontal ? mHalfCellSize : halfWidthInPx);
        mPickedShipRect.right = mPickedShipRect.left + (isHorizontal ? widthInPx : mCellSizePx);
        mPickedShipRect.bottom = mPickedShipRect.top + (isHorizontal ? mCellSizePx : widthInPx);
        return mPickedShipRect;
    }

    @NonNull
    private Vector2 getPickedShipCoordinate(@NonNull Rect pickedShipRect) {
        int shipInBoardCoordinatesX = pickedShipRect.left + mHalfCellSize;
        int shipInBoardCoordinatesY = pickedShipRect.top + mHalfCellSize;
        int i = xToI(shipInBoardCoordinatesX);
        int j = yToJ(shipInBoardCoordinatesY);
        return Vector2.get(i, j);
    }

    final int getCellSize() {
        return mCellSizePx;
    }
}
