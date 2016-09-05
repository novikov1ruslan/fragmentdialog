package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.AimingG;
import com.ivygames.morskoiboi.screen.view.BasePresenter;

final class SetupBoardPresenter extends BasePresenter {
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

    public SetupBoardPresenter(int boardSize, float turnBorderSize) {
        super(boardSize, turnBorderSize);
    }

    @Override
    protected void setBoardVerticalOffset(int offset) {
        super.setBoardVerticalOffset(offset);
    }

    @Override
    public void measure(int w, int h, int hPadding, int vPadding) {
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

    public boolean isInDockArea(Point p) {
        return isInDockArea(p.x, p.y);
    }

    public boolean isInDockArea(int x, int y) {
        return mShipSelectionRect.contains(x, y);
    }

    @NonNull
    public Point getShipDisplayAreaCenter() {
        return shipDisplayCenter;
    }

    public Rect getRectForCell(int i, int j) {
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

    public Rect getRectForDockedShip(@NonNull Ship ship) {
        Point p = getTopLeftPointInTopArea(ship.getSize());
        return getRectForShip(ship, p);
    }

    @NonNull
    private Point getTopLeftPointInTopArea(int shipSize) {
        int left = mShipSelectionRect.centerX() - getShipWidthInPx(shipSize) / 2;
        int top = mShipSelectionRect.centerY() - mCellSizePx / 2;
        return new Point(left, top);
    }

    @NonNull
    public Rect getPickedShipRect(@NonNull Ship ship, @NonNull Point p) {
        return getPickedShipRect(ship, p.x, p.y);
    }

    @NonNull
    public Rect getPickedShipRect(@NonNull Ship ship, int x, int y) {
        updatePickedGeometry(ship, x, y);
        return mPickedShipRect;
    }

    @NonNull
    public AimingG getAimingForShip(@NonNull Ship ship, int i, int j) {
        int width = ship.isHorizontal() ? ship.getSize() : 1;
        int height = ship.isHorizontal() ? 1 : ship.getSize();
        return getAimingG(i, j, width, height);
    }

    @NonNull
    public Vector2 updatePickedGeometry(@NonNull Ship ship, @NonNull Point p) {
        return updatePickedGeometry(ship, p.x, p.y);
    }

    @NonNull
    public Vector2 updatePickedGeometry(@NonNull Ship ship, int x, int y) {
        mPickedShipRect = centerPickedShipRectAround(ship, x, y);
        return getPickedShipCoordinate(mPickedShipRect);
    }

    private Rect centerPickedShipRectAround(@NonNull Ship ship, int x, int y) {
        int widthInPx = getShipWidthInPx(ship.getSize());
        int halfWidthInPx = widthInPx / 2;
        boolean isHorizontal = ship.isHorizontal();
        mPickedShipRect.left = x - (isHorizontal ? halfWidthInPx : mHalfCellSize);
        mPickedShipRect.top = y - (isHorizontal ? mHalfCellSize : halfWidthInPx);
        mPickedShipRect.right = mPickedShipRect.left + (isHorizontal ? widthInPx : mCellSizePx);
        mPickedShipRect.bottom = mPickedShipRect.top + (isHorizontal ? mCellSizePx : widthInPx);
        return mPickedShipRect;
    }

    private Vector2 getPickedShipCoordinate(Rect pickedShipRect) {
        int shipInBoardCoordinatesX = pickedShipRect.left + mHalfCellSize;
        int shipInBoardCoordinatesY = pickedShipRect.top + mHalfCellSize;
        int i = getTouchI(shipInBoardCoordinatesX);
        int j = getTouchJ(shipInBoardCoordinatesY);
        return Vector2.get(i, j);
    }

    public int getTouchI(int x) {
        return (x - mBoardRect.left) / mCellSizePx;
    }

    public int getTouchJ(int y) {
        return (y - mBoardRect.top) / mCellSizePx;
    }



    public boolean isOnBoard(@NonNull Point p) {
        return isOnBoard(p.x, p.y);
    }

    private boolean isOnBoard(int x, int y) {
        int i = getTouchI(x);
        int j = getTouchJ(y);
        return Board.contains(i, j);
    }

}
