package com.ivygames.morskoiboi.ui.view;

import android.graphics.Rect;
import android.graphics.RectF;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public class BasePresenter {

    private boolean mShowTurn;
    private float mTurnBorderSize;
    private Board mBoard;
    private Rect mTurnRect = new Rect(0, 0, 0, 0);
    protected int mCellSize;
    protected int mHalfCellSize;
    protected Rect mBoardRect = new Rect(0, 0, 0, 0);
    private int mMarkRadius;
    private float[] line = new float[4];
    private final Rect rect = new Rect();
    private final RectF rectF = new RectF();

    public void setBoard(Board board) {
        mBoard = board;
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

        mCellSize = smallestWidth / mBoard.getHorizontalDim();
        int boardSize = mCellSize * mBoard.getHorizontalDim();

        mBoardRect.left = (w - boardSize) / 2 + horOffset;
        mBoardRect.top = (h - boardSize) / 2 + verOffset;
        mBoardRect.right = mBoardRect.left + boardSize;
        mBoardRect.bottom = mBoardRect.top + boardSize;

        mHalfCellSize = mCellSize / 2;
        mMarkRadius = mHalfCellSize - mCellSize / 5;

        calcFrameRect();
    }

    public int getBoardWidth() {
        return mBoard.getHorizontalDim();
    }

    public int getBoardHeight() {
        return mBoard.getVerticalDim();
    }

    public Cell getCell(int x, int y) {
        return mBoard.getCell(x, y);
    }

    public boolean containsCell(int x, int y) {
        return mBoard.containsCell(x, y);
    }

    public Collection<Ship> getShips() {
        return mBoard.getShips();
    }

    public void rotateShipAt(int x, int y) {
        mBoard.rotateShipAt(x, y);
    }

    public Board getBoard() {
        return mBoard;
    }

    public Rect getTurnRect() {
        return mTurnRect;
    }

    public float[] getVertical(int i) {
        float startX = mBoardRect.left + i * mCellSize;
        float startY = mBoardRect.top;
        float stopY = mBoardRect.bottom;

        line[0] = startX;
        line[1] = startY;
        line[2] = startX;
        line[3] = stopY;

        return line;
    }

    public float[] getHorizontal(int i) {
        float startX = mBoardRect.left;
        float startY = mBoardRect.top + i * mCellSize;
        float stopX = mBoardRect.right;

        line[0] = startX;
        line[1] = startY;
        line[2] = stopX;
        line[3] = startY;

        return line;
    }

    public int getLeft(int i) {
        return i * mCellSize + mBoardRect.left;
    }

    public int getTop(int j) {
        return j * mCellSize + mBoardRect.top;
    }

    public Rect getBoardRect() {
        return mBoardRect;
    }

    public int getCellSize() {
        return mCellSize;
    }

    public Rect getVerticalRect(int i, int width) {
        int leftVer = mBoardRect.left + i * mCellSize;
        int rightVer = leftVer + width * mCellSize;
        if (rightVer > mBoardRect.right) {
            return null;
        }
        int topVer = mBoardRect.top;
        int bottomVer = mBoardRect.bottom;

        rect.left = leftVer;
        rect.right = rightVer;
        rect.top = topVer;
        rect.bottom = bottomVer;

        return rect;
    }

    public Rect getHorizontalRect(int j, int height) {
        int leftHor = mBoardRect.left;
        int rightHor = mBoardRect.right;
        int topHor = mBoardRect.top + j * mCellSize;
        int bottomHor = topHor + height * mCellSize;

        rect.left = leftHor;
        rect.right = rightHor;
        rect.top = topHor;
        rect.bottom = bottomHor;

        return rect;
    }

    // TODO: used in SetupBoardView
    public RectF getInvalidRect(int i, int j) {
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

    public int getCellY(int mTouchY) {
        return (mTouchY - mBoardRect.top) / mCellSize;
    }

    public int getCellX(int mTouchX) {
        return (mTouchX - mBoardRect.left) / mCellSize;
    }

    public float getMarkOuterRadius() {
        return mMarkRadius;
    }

    public float getMarkInnerRadius() {
        return (float) mMarkRadius - mCellSize / 6;
    }

    public boolean isTurn() {
        return mShowTurn;
    }

    public void showTurn() {
        mShowTurn = true;
    }

    public void hideTurn() {
        mShowTurn = false;
    }

    @Override
    public String toString() {
        return mBoard.toString();
    }
}
