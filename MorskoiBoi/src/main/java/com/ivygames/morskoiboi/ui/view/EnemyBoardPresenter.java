package com.ivygames.morskoiboi.ui.view;

import android.graphics.Rect;

import com.ivygames.morskoiboi.Animation;

/**
 * Created by novikov on 2/7/16.
 */
class EnemyBoardPresenter extends BasePresenter {
    private int mAnimationHorOffset;
    private int mAnimationVerOffset;
    private final Rect mDstRect = new Rect();

    public EnemyBoardPresenter(int boardSize, float turnBorderSize) {
        super(boardSize, turnBorderSize);
    }

    @Override
    public void measure(int w, int h, int horOffset, int verOffset, int smallestWidth) {
        super.measure(w, h, horOffset, verOffset, smallestWidth);
        mAnimationHorOffset = mBoardRect.left + mHalfCellSize;
        mAnimationVerOffset = mBoardRect.top + mHalfCellSize;
    }

    public Rect getAnimationDestination(Animation animation) {
        int dx = animation.getAim().getX() * mCellSizePx + mAnimationHorOffset;
        int dy = animation.getAim().getY() * mCellSizePx + mAnimationVerOffset;

        int d = (int) (animation.getCellRatio() * mHalfCellSize);
        mDstRect.left = dx - d;
        mDstRect.top = dy - d;
        mDstRect.right = dx + d;
        mDstRect.bottom = dy + d;

        return mDstRect;
    }
}
