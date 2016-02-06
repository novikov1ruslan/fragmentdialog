package com.ivygames.morskoiboi.ui.view;

import android.graphics.Rect;
import android.graphics.RectF;

public class BasePresenter {

    private boolean mShowTurn;
    private float mTurnBorderSize;
    private Rect mTurnRect = new Rect(0, 0, 0, 0);
    protected int mCellSize;
    protected int mHalfCellSize;
    protected Rect mBoardRect = new Rect(0, 0, 0, 0);
    private int mMarkRadius;
    private float[] line = new float[4];
    private final Rect hRect = new Rect();
    private final Rect vRect = new Rect();
    private final RectF rectF = new RectF();
    private int mBoardSize;

    public void setBoardSize(int size) {
        mBoardSize = size;
    }

    public void setTurnBorderSize(float dimension) {
        mTurnBorderSize = dimension;
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

    public final void calculateBoardRect(int w, int h, int horOffset, int verOffset) {

        int smallestWidth = w < h ? w : h;

        mCellSize = smallestWidth / mBoardSize;
        int boardSize = mCellSize * mBoardSize;

        mBoardRect.left = (w - boardSize) / 2 + horOffset;
        mBoardRect.top = (h - boardSize) / 2 + verOffset;
        mBoardRect.right = mBoardRect.left + boardSize;
        mBoardRect.bottom = mBoardRect.top + boardSize;

        mHalfCellSize = mCellSize / 2;
        mMarkRadius = mHalfCellSize - mCellSize / 5;

        calcFrameRect();
    }

    public final Rect getTurnRect() {
        return mTurnRect;
    }

    public final float[] getVertical(int i) {
        float startX = mBoardRect.left + i * mCellSize;
        float startY = mBoardRect.top;
        float stopY = mBoardRect.bottom;

        line[0] = startX;
        line[1] = startY;
        line[2] = startX;
        line[3] = stopY;

        return line;
    }

    public final float[] getHorizontal(int i) {
        float startX = mBoardRect.left;
        float startY = mBoardRect.top + i * mCellSize;
        float stopX = mBoardRect.right;

        line[0] = startX;
        line[1] = startY;
        line[2] = stopX;
        line[3] = startY;

        return line;
    }

    public final int getLeft(int i) {
        return i * mCellSize + mBoardRect.left;
    }

    public final int getTop(int j) {
        return j * mCellSize + mBoardRect.top;
    }

    public final Rect getBoardRect() {
        return mBoardRect;
    }

    public final int getCellSize() {
        return mCellSize;
    }

    public final Rect getVerticalRect(int i, int width) {
        int leftVer = mBoardRect.left + i * mCellSize;
        int rightVer = leftVer + width * mCellSize;
        if (rightVer > mBoardRect.right) {
            return null;
        }
        int topVer = mBoardRect.top;
        int bottomVer = mBoardRect.bottom;

        vRect.left = leftVer;
        vRect.right = rightVer;
        vRect.top = topVer;
        vRect.bottom = bottomVer;

        return vRect;
    }

    public final Rect getHorizontalRect(int j, int height) {
        int leftHor = mBoardRect.left;
        int rightHor = mBoardRect.right;
        int topHor = mBoardRect.top + j * mCellSize;
        int bottomHor = topHor + height * mCellSize;

        hRect.left = leftHor;
        hRect.right = rightHor;
        hRect.top = topHor;
        hRect.bottom = bottomHor;

        return hRect;
    }

    // TODO: used in SetupBoardView
    public final RectF getInvalidRect(int i, int j) {
        float left = mBoardRect.left + i * mCellSize + 1;
        float top = mBoardRect.top + j * mCellSize + 1;
        float right = left + mCellSize;
        float bottom = top + mCellSize;

        rectF.left = left + 1;
        rectF.top = top + 1;
        rectF.right = right;
        rectF.bottom = bottom;

        return rectF;
    }

    public final int getCellY(int mTouchY) {
        return (mTouchY - mBoardRect.top) / mCellSize;
    }

    public final int getCellX(int mTouchX) {
        return (mTouchX - mBoardRect.left) / mCellSize;
    }

    public final float getMarkOuterRadius() {
        return mMarkRadius;
    }

    public final float getMarkInnerRadius() {
        return (float) mMarkRadius - mCellSize / 6;
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

}
