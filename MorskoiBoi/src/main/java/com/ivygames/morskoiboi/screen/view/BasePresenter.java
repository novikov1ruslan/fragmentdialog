package com.ivygames.morskoiboi.screen.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class BasePresenter {

    private final int mBoardSize;
    private final float mTurnBorderSize;

    private final Rect mTurnRect = new Rect();
    private int mMarkRadius;

    protected int mCellSizePx;
    protected int mHalfCellSize;
    @NonNull
    protected Rect mBoardRect = new Rect();

    private final Rect hRect = new Rect();
    private final Rect vRect = new Rect();
    private final Mark mMark = new Mark();
    private final Aiming mAiming = new Aiming();
    private BoardG mBoard;

    public BasePresenter(int boardSize, float turnBorderSize) {
        mBoardSize = boardSize;
        mTurnBorderSize = turnBorderSize;
    }

    /**
     * Called during {@link android.view.View#onLayout(boolean, int, int, int, int)}
     */
    public void measure(int w, int h, int hPadding, int vPadding) {

        int smallestWidth = calcSmallestWidth(w, h, hPadding, vPadding);
        mCellSizePx = smallestWidth / mBoardSize;
        Ln.d("cell size= " + mCellSizePx + "; w=" + w + "; h=" + h);
        int boardSizePx = mCellSizePx * mBoardSize;

        mBoardRect = calculateBoardRect(w, h, boardSizePx);

        mHalfCellSize = mCellSizePx / 2;
        mMarkRadius = mHalfCellSize - mCellSizePx / 5;

        setBoardVerticalOffset(0);
    }

    /**
     * called during {@link #measure(int, int, int, int)}t
     */
    protected void setBoardVerticalOffset(int offset) {
        mBoardRect.top += offset;
        mBoardRect.bottom += offset;

        calcFrameRect(mBoardRect);
        calculateBoardG();
    }

    private int calcSmallestWidth(int w, int h, int hPadding, int vPadding) {
        int paddedWidth = w - hPadding;
        int paddedHeight = h - vPadding;

        return paddedWidth < paddedHeight ? paddedWidth : paddedHeight;
    }

    private Rect calculateBoardRect(int w, int h, int boardSize) {
        mBoardRect.left = (w - boardSize) / 2;
        mBoardRect.top = (h - boardSize) / 2;
        mBoardRect.right = mBoardRect.left + boardSize;
        mBoardRect.bottom = mBoardRect.top + boardSize;

        return mBoardRect;
    }

    /**
     * Frame Rect is larger by border
     */
    private void calcFrameRect(@NonNull Rect boardRect) {
        float halfBorderSize = mTurnBorderSize / 2;
        mTurnRect.left = (int) (boardRect.left - halfBorderSize);
        mTurnRect.right = (int) (boardRect.right + halfBorderSize);
        mTurnRect.top = (int) (boardRect.top - halfBorderSize);
        mTurnRect.bottom = (int) (boardRect.bottom + halfBorderSize);
    }

    private float[] getVertical(int i) {
        float startX = mBoardRect.left + i * mCellSizePx;
        float startY = mBoardRect.top;
        float stopY = mBoardRect.bottom;

        float[] line = new float[4];
        line[0] = startX;
        line[1] = startY;
        line[2] = startX;
        line[3] = stopY;

        return line;
    }

    private float[] getHorizontal(int i) {
        float startX = mBoardRect.left;
        float startY = mBoardRect.top + i * mCellSizePx;
        float stopX = mBoardRect.right;

        float[] line = new float[4];
        line[0] = startX;
        line[1] = startY;
        line[2] = stopX;
        line[3] = startY;

        return line;
    }

    public Mark getMark(int x, int y) {
        isTrue(mCellSizePx > 0, "call measure first");

        int left = getLeft(x);
        int top = getTop(y);
        mMark.centerX = left + mHalfCellSize;
        mMark.centerY = top + mHalfCellSize;
        mMark.outerRadius = getMarkOuterRadius();
        mMark.innerRadius = getMarkInnerRadius();

        return mMark;
    }

    private int getLeft(int i) {
        return i * mCellSizePx + mBoardRect.left;
    }

    private int getTop(int j) {
        return j * mCellSizePx + mBoardRect.top;
    }

    private float getMarkOuterRadius() {
        return mMarkRadius;
    }

    private float getMarkInnerRadius() {
        return (float) mMarkRadius - mCellSizePx / 6;
    }

    @NonNull
    public final Aiming getAiming(@NonNull Vector2 aim, int widthCells, int heightCells) {
        return getAiming(aim.getX(), aim.getY(), widthCells, heightCells);
    }

    @NonNull
    public final Aiming getAiming(int i, int j, int widthCells, int heightCells) {
        Validate.isTrue(widthCells > 0 && heightCells > 0);

        mAiming.vertical = getVerticalRect(i, widthCells);
        if (mAiming.vertical.right > mBoardRect.right) {
            mAiming.vertical.right = mBoardRect.right;
        }
        mAiming.horizontal = getHorizontalRect(j, heightCells);
        if (mAiming.horizontal.bottom > mBoardRect.bottom) {
            mAiming.horizontal.bottom = mBoardRect.bottom;
        }
        return mAiming;
    }

    @NonNull
    private Rect getVerticalRect(int i, int widthCells) {
        int leftVer = mBoardRect.left + i * mCellSizePx;
        int rightVer = leftVer + widthCells * mCellSizePx;
        int topVer = mBoardRect.top;
        int bottomVer = mBoardRect.bottom;

        vRect.left = leftVer;
        vRect.right = rightVer;
        vRect.top = topVer;
        vRect.bottom = bottomVer;

        return vRect;
    }

    @NonNull
    private Rect getHorizontalRect(int j, int heightCells) {
        int leftHor = mBoardRect.left;
        int rightHor = mBoardRect.right;
        int topHor = mBoardRect.top + j * mCellSizePx;
        int bottomHor = topHor + heightCells * mCellSizePx;

        hRect.left = leftHor;
        hRect.right = rightHor;
        hRect.top = topHor;
        hRect.bottom = bottomHor;

        return hRect;
    }

    public BoardG getBoard() {
        return mBoard;
    }

    private final Rect mShipRect = new Rect();

    public Rect getRectForShip(@NonNull Ship ship) {
        return getRectForShip(ship, getLeft(ship.getX()), getTop(ship.getY()));
    }

    public Rect getRectForShip(@NonNull Ship ship, @NonNull Point point) {
        return getRectForShip(ship, point.x, point.y);
    }

    private Rect getRectForShip(@NonNull Ship ship, int left, int top) {
        isTrue(mCellSizePx > 0, "call measure first");

        mShipRect.left = left;
        mShipRect.top = top;

        int shipSize = ship.getSize();
        if (ship.isHorizontal()) {
            mShipRect.right = mShipRect.left + mCellSizePx * shipSize;
            mShipRect.bottom = mShipRect.top + mCellSizePx;
        } else {
            mShipRect.right = mShipRect.left + mCellSizePx;
            mShipRect.bottom = mShipRect.top + mCellSizePx * shipSize;
        }

        return mShipRect;
    }

    private void calculateBoardG() {
        mBoard = new BoardG();
        for (int i = 0; i < mBoardSize + 1; i++) {
            mBoard.lines[i] = getVertical(i);
        }

        for (int i = 0; i < mBoardSize + 1; i++) {
            mBoard.lines[mBoardSize + i] = getHorizontal(i);
        }

        mBoard.frame = mTurnRect;
    }

    private static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
}
