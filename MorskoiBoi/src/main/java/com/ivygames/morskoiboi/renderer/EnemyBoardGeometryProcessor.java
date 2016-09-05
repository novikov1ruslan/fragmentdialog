package com.ivygames.morskoiboi.renderer;

import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Vector2;

public final class EnemyBoardGeometryProcessor extends BaseGeometryProcessor {

    private int mAnimationHorOffset;
    private int mAnimationVerOffset;
    @NonNull
    private final Rect mDstRect = new Rect();
    @NonNull
    private final Rect mLockDstRect = new Rect();

    public EnemyBoardGeometryProcessor(int boardSize, float turnBorderSize) {
        super(boardSize, turnBorderSize);
    }

    @Override
    protected void measure(int w, int h, int hPadding, int vPadding) {
        super.measure(w, h, hPadding, vPadding);
        mAnimationVerOffset = mBoardRect.top + mHalfCellSize;
        mAnimationHorOffset = mBoardRect.left + mHalfCellSize;
    }

    @Override
    protected void setBoardVerticalOffset(int offset) {
        super.setBoardVerticalOffset(offset);
        mAnimationVerOffset = mBoardRect.top + mHalfCellSize;
    }

    @NonNull
    final Rect getAnimationDestination(@NonNull Vector2 aim, float cellRatio) {
        int dx = aim.getX() * mCellSizePx + mAnimationHorOffset;
        int dy = aim.getY() * mCellSizePx + mAnimationVerOffset;

        int d = (int) (cellRatio * mHalfCellSize);
        mDstRect.left = dx - d;
        mDstRect.top = dy - d;
        mDstRect.right = dx + d;
        mDstRect.bottom = dy + d;

        return mDstRect;
    }

    @NonNull
    final Rect getAimRectDst(@NonNull Vector2 aim) {
        int x = aim.getX();
        int y = aim.getY();
        int left = x * mCellSizePx + mBoardRect.left;
        int top = y * mCellSizePx + mBoardRect.top;
        mLockDstRect.left = left;
        mLockDstRect.top = top;
        mLockDstRect.right = left + mCellSizePx;
        mLockDstRect.bottom = top + mCellSizePx;

        return mLockDstRect;
    }

    @NonNull
    public Rect getBoardRect() {
        return mBoardRect;
    }

}
