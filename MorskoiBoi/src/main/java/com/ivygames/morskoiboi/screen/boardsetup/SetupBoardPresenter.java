package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.Aiming;
import com.ivygames.morskoiboi.screen.view.BasePresenter;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.util.PriorityQueue;

final class SetupBoardPresenter extends BasePresenter {
    private final Rect mShipSelectionRect = new Rect();
    private final Rect mShipDisplayRect = new Rect();
    private final Point shipDisplayCenter = new Point();
    private Rect mPickedShipRect = new Rect();
    private final Rect mRectForCell = new Rect();
    private final Placement mPlacementAlgorithm = PlacementFactory.getAlgorithm();
    /**
     * ship displayed at the top of the screen (selection area)
     */
    private Ship mDockedShip;

    private PriorityQueue<Ship> mShips;

    /**
     * currently picked ship (awaiting to be placed)
     */
    private Ship mPickedShip;

    @NonNull
    private Vector2 mAim = Vector2.INVALID_VECTOR;
    private int mX;
    private int mY;
//    private int mI;
//    private int mJ;

    public SetupBoardPresenter(int boardSize, float dimension) {
        super(boardSize, dimension);
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

    public Ship getDockedShip() {
        return mDockedShip;
    }

    public Rect getRectForDockedShip() {
        Point p = getTopLeftPointInTopArea(mDockedShip.getSize());
        return getRectForShip(mDockedShip, p);
    }

    @NonNull
    private Point getTopLeftPointInTopArea(int shipSize) {
        int left = mShipSelectionRect.centerX() - getShipWidthInPx(shipSize) / 2;
        int top = mShipSelectionRect.centerY() - mCellSizePx / 2;
        return new Point(left, top);
    }

    @Nullable
    public Rect getPickedShipRect() {
        return hasPickedShip() ? mPickedShipRect : null;
    }

    @Nullable
    public Aiming getAiming() {
        if (hasPickedShip() && Board.containsCell(mAim)) {
            return getAimingForPickedShip();
        }

        return null;
    }

    @NonNull
    private Aiming getAimingForPickedShip() {
        return getAimingForShip(mPickedShip);
    }

    @NonNull
    private Aiming getAimingForShip(@NonNull Ship ship) {
        int width = ship.isHorizontal() ? ship.getSize() : 1;
        int height = mPickedShip.isHorizontal() ? 1 : mPickedShip.getSize();
        return getAiming(mAim, width, height);
    }

    public boolean hasPickedShip() {
        return mPickedShip != null;
    }

    public void pickDockedShip() {
        mPickedShip = mShips.poll();
        if (mPickedShip == null) {
            Ln.v("no ships to pick");
        } else {
            mDockedShip = null;
            Ln.v(mPickedShip + " picked from stack, stack: " + mShips);
        }

        if (hasPickedShip()) {
            updatePickedGeometry();
        }
    }


    public void touch(Point p) {
        touch(p.x, p.y);
    }

    public void touch(int x, int y) {
        mX = x;
        mY = y;
        if (hasPickedShip()) {
            updatePickedGeometry();
        }
    }

    private void updatePickedGeometry() {
        mPickedShipRect = centerPickedShipRectAround(mPickedShip, mX, mY);
        mAim = getPickedShipCoordinate(mPickedShipRect);
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
        int shipInBoardCoordinatesX = pickedShipRect.left - mBoardRect.left + mHalfCellSize;
        int shipInBoardCoordinatesY = pickedShipRect.top - mBoardRect.top + mHalfCellSize;
        int i = shipInBoardCoordinatesX / mCellSizePx;
        int j = shipInBoardCoordinatesY / mCellSizePx;
        return Vector2.get(i, j);
    }

    public void dropShip(@NonNull Board board) {
        if (mPickedShip == null) {
            return;
        }

        if (!tryPlaceShip(board, mPickedShip, mAim)) {
            returnShipToPool(mPickedShip);
        }

        setDockedShip();
        mPickedShip = null;
    }

    /**
     * @return true if succeeded to put down currently picked-up ship
     */
    private boolean tryPlaceShip(@NonNull Board board, @NonNull Ship ship, @NonNull Vector2 aim) {
        if (board.shipFitsTheBoard(ship, aim)) {
            mPlacementAlgorithm.putShipAt(board, ship, aim.getX(), aim.getY());
            return true;
        }
        return false;
    }

    private void returnShipToPool(@NonNull Ship ship) {
        if (!ship.isHorizontal()) {
            ship.rotate();
        }
        mShips.add(ship);
    }

    private void setDockedShip() {
        if (mShips.isEmpty()) {
            mDockedShip = null;
        } else {
            mDockedShip = mShips.peek();
        }
    }

    public void pickShipFromBoard(Board board, Point p) {
        pickShipFromBoard(board, p.x, p.y);
    }

    public void pickShipFromBoard(@NonNull Board board, int x, int y) {
        final int i = getTouchI(x);
        final int j = getTouchJ(y);
        mPickedShip = board.removeShipFrom(i, j);
    }

    public void rotateShipAt(Board board, Point p) {
        rotateShipAt(board, p.x, p.y);
    }

    public void rotateShipAt(@NonNull Board board, int x, int y) {
        int i = getTouchI(x);
        int j = getTouchJ(y);
        board.rotateShipAt(i, j);
    }

    public boolean isOnBoard(Point p) {
        return isOnBoard(p.x, p.y);
    }

    public boolean isOnBoard(int x, int y) {
        int i = getTouchI(x);
        int j = getTouchJ(y);
        return Board.containsCell(i, j);
    }

    private int getTouchJ(int y) {
        return (y - mBoardRect.top) / mCellSizePx;
    }

    private int getTouchI(int x) {
        return (x - mBoardRect.left) / mCellSizePx;
    }

    public void setFleet(@NonNull PriorityQueue<Ship> ships) {
        mShips = Validate.notNull(ships);
        setDockedShip();
    }
}
