package com.ivygames.morskoiboi.scenario;

import com.ivygames.morskoiboi.renderer.BaseGeometryProcessor;

import static android.R.attr.y;

class MyProcessor extends BaseGeometryProcessor {

    private final int mBoardSize;
    private final float mTurnBorderSize;

    public MyProcessor(int boardSize, float turnBorderSize) {
        super(boardSize, turnBorderSize);
        mBoardSize = boardSize;
        mTurnBorderSize = turnBorderSize;
    }

    @Override
    protected void measure(int w, int h, int hPadding, int vPadding) {
        super.measure(w, h, hPadding, vPadding);
    }

    public int getX(int i) {
        int cellSize = mBoardRect.width() / mBoardSize;
        return (int) (i * cellSize + mTurnBorderSize);
    }

    public int getY(int j) {
        int cellSize = mBoardRect.width() / mBoardSize;
        return (int) (j * cellSize + mTurnBorderSize);
    }
}
