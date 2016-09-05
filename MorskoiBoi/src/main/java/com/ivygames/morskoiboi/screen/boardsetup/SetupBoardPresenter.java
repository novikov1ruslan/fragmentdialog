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
import com.ivygames.morskoiboi.screen.view.AimingG;
import com.ivygames.morskoiboi.screen.view.BasePresenter;

import org.commons.logger.Ln;

import java.util.PriorityQueue;

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
    @NonNull
    private final Placement mPlacement = PlacementFactory.getAlgorithm();
    /**
     * ship displayed at the top of the screen (selection area)
     */
    private Ship mDockedShip;

    private PriorityQueue<Ship> mDockedShips;

    /**
     * currently picked ship (awaiting to be placed)
     */
    private Ship mPickedShip;

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

    public Ship getDockedShip() {
        return mDockedShip;
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

    public boolean hasPickedShip() {
        return mPickedShip != null;
    }

    public void pickDockedShip() {
        mPickedShip = mDockedShips.poll();
        if (mPickedShip == null) {
            Ln.v("no ships to pick");
        } else {
            mDockedShip = null;
            Ln.v(mPickedShip + " picked from stack, stack: " + mDockedShips);
        }
    }

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

    public void dropShip(@NonNull Board board, @NonNull Vector2 coordinate) {
        dropShip(board, coordinate.getX(), coordinate.getY());
    }

    public void dropShip(@NonNull Board board, int i, int j) {
        if (mPickedShip == null) {
            return;
        }

        if (board.shipFitsTheBoard(mPickedShip, i, j)) {
            mPlacement.putShipAt(board, mPickedShip, i, j);
        } else {
            returnShipToPool(mPickedShip);
        }

        setDockedShip();
        mPickedShip = null;
    }

    private void returnShipToPool(@NonNull Ship ship) {
        if (!ship.isHorizontal()) {
            ship.rotate();
        }
        mDockedShips.add(ship);
    }

    public void setDockedShip() {
        if (mDockedShips.isEmpty()) {
            mDockedShip = null;
        } else {
            mDockedShip = mDockedShips.peek();
        }
    }

    public void pickShipFromBoard(@NonNull Board board, @NonNull Vector2 coordinate) {
        pickShipFromBoard(board, coordinate.getX(), coordinate.getY());
    }

    public void pickShipFromBoard(@NonNull Board board, int i, int j) {
        mPickedShip = mPlacement.removeShipFrom(board, i, j);
    }

    public void rotateShipAt(@NonNull Board board, @NonNull Vector2 aim) {
        rotateShipAt(board, aim.getX(), aim.getY());
    }

    public void rotateShipAt(@NonNull Board board, int i, int j) {
        mPlacement.rotateShipAt(board, i, j);
    }

    public boolean isOnBoard(@NonNull Point p) {
        return isOnBoard(p.x, p.y);
    }

    private boolean isOnBoard(int x, int y) {
        int i = getTouchI(x);
        int j = getTouchJ(y);
        return Board.contains(i, j);
    }

    public void setFleet(@NonNull PriorityQueue<Ship> ships) {
        mDockedShips = ships;
        setDockedShip();
    }

    @Nullable
    public Ship getPickedShip() {
        return mPickedShip;
    }
}
