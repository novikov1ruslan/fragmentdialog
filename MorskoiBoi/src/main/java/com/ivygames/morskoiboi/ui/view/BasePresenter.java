package com.ivygames.morskoiboi.ui.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Ship;

public class BasePresenter {

    private final int mBoardSize;
    private final float mTurnBorderSize;

    private boolean mShowTurn;
    private Rect mTurnRect = new Rect();
    private int mMarkRadius;

    protected int mCellSizePx;
    protected int mHalfCellSize;
    protected Rect mBoardRect = new Rect();

    // temporary fields
    private final Rect hRect = new Rect();
    private final Rect vRect = new Rect();
    // TODO: SetupBoardView
    private final RectF rectF = new RectF();
    private final Mark mMark = new Mark();
    private final Aiming mAiming = new Aiming();
    private BoardG mBoard;

    public BasePresenter(int boardSize, float turnBorderSize) {
        mBoardSize = boardSize;
        mTurnBorderSize = turnBorderSize;
    }

    public void measure(int w, int h, int horOffset, int verOffset, int hPadding, int vPadding) {

        int smallestWidth = calcSmallestWidth(w, h, hPadding, vPadding);
        mCellSizePx = smallestWidth / mBoardSize;
        int boardSizePx = mCellSizePx * mBoardSize;

        calculateBoardRect(w, h, horOffset, verOffset, boardSizePx);

        mHalfCellSize = mCellSizePx / 2;
        mMarkRadius = mHalfCellSize - mCellSizePx / 5;

        calcFrameRect();
        calculateBoardG();
    }

    private int calcSmallestWidth(int w, int h, int hPadding, int vPadding) {
        int paddedWidth = w - hPadding;
        int paddedHeight = h - vPadding;

        return paddedWidth < paddedHeight ? paddedWidth : paddedHeight;
    }

    private void calculateBoardRect(int w, int h, int horOffset, int verOffset, int boardSize) {
        mBoardRect.left = (w - boardSize) / 2 + horOffset;
        mBoardRect.top = (h - boardSize) / 2 + verOffset;
        mBoardRect.right = mBoardRect.left + boardSize;
        mBoardRect.bottom = mBoardRect.top + boardSize;
    }

    /**
     * Frame Rect is larger by border
     */
    private void calcFrameRect() {
        mTurnRect.left = (int) (mBoardRect.left - mTurnBorderSize / 2);
        mTurnRect.right = (int) (mBoardRect.right + mTurnBorderSize / 2);
        mTurnRect.top = (int) (mBoardRect.top - mTurnBorderSize / 2);
        mTurnRect.bottom = (int) (mBoardRect.bottom + mTurnBorderSize / 2);
    }

    private final Rect getTurnRect() {
        return mTurnRect;
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
        int left = getLeft(x);
        int top = getTop(y);
        mMark.centerX = left + mHalfCellSize;
        mMark.centerY = top + mHalfCellSize;
        mMark.outerRadius = getMarkOuterRadius();
        mMark.innerRadius = getMarkInnerRadius();

        return mMark;
    }

    private final int getLeft(int i) {
        return i * mCellSizePx + mBoardRect.left;
    }

    private final int getTop(int j) {
        return j * mCellSizePx + mBoardRect.top;
    }

    private final int getCellSize() {
        return mCellSizePx;
    }

    private
    @NonNull
    Rect getVerticalRect(int i, int width) {
        int leftVer = mBoardRect.left + i * mCellSizePx;
        int rightVer = leftVer + width * mCellSizePx;
        int topVer = mBoardRect.top;
        int bottomVer = mBoardRect.bottom;

        vRect.left = leftVer;
        vRect.right = rightVer;
        vRect.top = topVer;
        vRect.bottom = bottomVer;

        return vRect;
    }

    private
    @NonNull
    Rect getHorizontalRect(int j, int height) {
        int leftHor = mBoardRect.left;
        int rightHor = mBoardRect.right;
        int topHor = mBoardRect.top + j * mCellSizePx;
        int bottomHor = topHor + height * mCellSizePx;

        hRect.left = leftHor;
        hRect.right = rightHor;
        hRect.top = topHor;
        hRect.bottom = bottomHor;

        return hRect;
    }

    // TODO: used in SetupBoardView
    public final RectF getInvalidRect(int i, int j) {
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

    public final int getCellY(int mTouchY) {
        return (mTouchY - mBoardRect.top) / mCellSizePx;
    }

    public final int getCellX(int mTouchX) {
        return (mTouchX - mBoardRect.left) / mCellSizePx;
    }

    private float getMarkOuterRadius() {
        return mMarkRadius;
    }

    private float getMarkInnerRadius() {
        return (float) mMarkRadius - mCellSizePx / 6;
    }

    public final boolean isTurn() {
        return mShowTurn;
    }

    public final void showTurn() {
        mShowTurn = true;
    }

    public final void hideTurn() {
        mShowTurn = false;
    }

    public
    @NonNull
    Aiming getAiming(int i, int j, int width, int height) {
        mAiming.vertical = getVerticalRect(i, width);
        if (mAiming.vertical.right > mBoardRect.right) {
            mAiming.vertical.right = mBoardRect.right;
        }
        mAiming.horizontal = getHorizontalRect(j, height);
        if (mAiming.horizontal.bottom > mBoardRect.bottom) {
            mAiming.horizontal.bottom = mBoardRect.bottom;
        }
        return mAiming;
    }

    public BoardG getBoard() {
        return mBoard;
    }

    private Rect mShipRect = new Rect();

    public Rect getRectForShip(Ship ship) {
        return getRectForShip(ship, getLeft(ship.getX()), getTop(ship.getY()));
    }

    public Rect getRectForShip(Ship ship, Point point) {
        return getRectForShip(ship, point.x, point.y);
    }

    public Rect getRectForShip(Ship ship, int left, int top) {
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

        mBoard.frame = getTurnRect();
    }
}
