package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
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
    private final Rect mPickedShipRect = new Rect();
    private final RectF rectF = new RectF();
    private final PlacementAlgorithm mPlacementAlgorithm = PlacementFactory.getAlgorithm();

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

    private boolean isInShipSelectionArea(int x, int y) {
        return mShipSelectionRect.contains(x, y);
    }

    @NonNull
    public Point getTopLeftPointInTopArea(int shipSize) {
        int left = mShipSelectionRect.centerX() - getShipWidthInPx(shipSize) / 2;
        int top = mShipSelectionRect.centerY() - mCellSizePx / 2;
        return new Point(left, top);
    }

    public Point getShipDisplayAreaCenter() {
        return shipDisplayCenter;
    }

    private Vector2 getPickedShipCoordinate() {
        int shipInBoardCoordinatesX = mPickedShipRect.left - mBoardRect.left + mHalfCellSize;
        int shipInBoardCoordinatesY = mPickedShipRect.top - mBoardRect.top + mHalfCellSize;
        int i = shipInBoardCoordinatesX / mCellSizePx;
        int j = shipInBoardCoordinatesY / mCellSizePx;
        return Vector2.get(i, j);
    }

    private void centerPickedShipRectAround(@NonNull Ship ship, int x, int y) {
        int widthInPx = getShipWidthInPx(ship.getSize());
        int halfWidthInPx = widthInPx / 2;
        boolean isHorizontal = ship.isHorizontal();
        mPickedShipRect.left = x - (isHorizontal ? halfWidthInPx : mHalfCellSize);
        mPickedShipRect.top = y - (isHorizontal ? mHalfCellSize : halfWidthInPx);
        mPickedShipRect.right = mPickedShipRect.left + (isHorizontal ? widthInPx : mCellSizePx);
        mPickedShipRect.bottom = mPickedShipRect.top + (isHorizontal ? mCellSizePx : widthInPx);
    }

    @NonNull
    public Aiming getAimingForPickedShip(Vector2 mAim, Ship mPickedShip) {
        int width = mPickedShip.isHorizontal() ? mPickedShip.getSize() : 1;
        int height = mPickedShip.isHorizontal() ? 1 : mPickedShip.getSize();
        return getAiming(mAim, width, height);
    }

    public Vector2 getAimForShip(@NonNull Ship ship, int x, int y) {
        centerPickedShipRectAround(ship, x, y);
        return getPickedShipCoordinate();
    }

    private int getTouchJ(int y) {
        return (y - mBoardRect.top) / mCellSizePx;
    }

    private int getTouchI(int x) {
        return (x - mBoardRect.left) / mCellSizePx;
    }

    public boolean isInShipSelectionArea(MotionEvent event) {
        return isInShipSelectionArea((int) event.getX(), (int) event.getY());
    }

    public final RectF getRectFor(int i, int j) {
        float left = mBoardRect.left + i * mCellSizePx + 1;
        float top = mBoardRect.top + j * mCellSizePx + 1;
        float right = left + mCellSizePx;
        float bottom = top + mCellSizePx;

        rectF.left = left + 1;
        rectF.top = top + 1;
        rectF.right = right;
        rectF.bottom = bottom;

        return rectF;
    }

    /**
     * ship displayed at the top of the screen (selection area)
     */
    private Ship mDockedShip;

    public Ship getDockedShip() {
        return mDockedShip;
    }

    public Rect getRectForDockedShip() {
        Point p = getTopLeftPointInTopArea(mDockedShip.getSize());
        return getRectForShip(mDockedShip, p);
    }

    public void pickDockedShip() {
        mDockedShip = null;
    }

    public void setDockedShip(PriorityQueue<Ship> ships) {
        if (ships.isEmpty()) {
            mDockedShip = null;
        } else {
            mDockedShip = ships.peek();
        }
    }

    private PriorityQueue<Ship> mShips;

    /**
     * currently picked ship (awaiting to be placed)
     */
    private Ship mPickedShip;
    private Vector2 mAim = Vector2.get(-1, -1);

    public @Nullable
    Rect getPickedShipRect() {
        return mPickedShip == null ? null : mPickedShipRect;
    }

    public Aiming getAiming(@NonNull Board board) {
        if (hasPickedShip() && board.containsCell(mAim)) {
            return getAimingForPickedShip(mAim, mPickedShip);
        }

        return null;
    }

    public boolean hasPickedShip() {
        return mPickedShip != null;
    }

    public void updateAim(int x, int y) {
        if (hasPickedShip()) {
            mAim = getAimForShip(mPickedShip, x, y);
        }
    }

    public void dropShip(@NonNull Board board) {
        if (mPickedShip == null) {
            return;
        }

        if (!tryPlaceShip(board, mPickedShip)) {
            returnShipToPool(mPickedShip);
        }

        setDockedShip(mShips);
        mPickedShip = null;
    }

    public void pickShipFromBoard(@NonNull Board board, int x, int y) {
        final int i = getTouchI(x);
        final int j = getTouchJ(y);
        mPickedShip = board.removeShipFrom(i, j);
    }

    public void rotateShipAt(@NonNull Board board, int x, int y) {
        int i = getTouchI(x);
        int j = getTouchJ(y);
        board.rotateShipAt(i, j);
    }

    public boolean isOnBoard(@NonNull Board board, int x, int y) {
        int i = getTouchI(x);
        int j = getTouchJ(y);
        return board.containsCell(i, j);
    }

    public void pickDockedShipUp(int x, int y) {
        mPickedShip = mShips.poll();
        if (mPickedShip == null) {
            Ln.v("no ships to pick");
        } else {
            pickDockedShip();
            updateAim(x, y);
            Ln.v(mPickedShip + " picked from stack, stack: " + mShips);
        }
    }

    /**
     * @return true if succeeded to put down currently picked-up ship
     */
    private boolean tryPlaceShip(@NonNull Board board, @NonNull Ship ship) {
        if (board.shipFitsTheBoard(ship, mAim)) {
            mPlacementAlgorithm.putShipAt(board, ship, mAim.getX(), mAim.getY());
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

    public void setFleet(PriorityQueue<Ship> ships) {
        mShips = Validate.notNull(ships);
        setDockedShip(mShips);
    }
}
