package com.ivygames.morskoiboi.ui.view;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.Animation;
import com.ivygames.morskoiboi.model.Vector2;

public final class EnemyBoardPresenter extends BasePresenter {

    private static final int LEFT_MARGIN = 10;

    private int mAnimationHorOffset;
    private int mAnimationVerOffset;
    private final Rect mDstRect = new Rect();
    private Rect mLockDstRect = new Rect();

    private int mTouchX;
    private int mTouchY;

    private Vector2 mAim;
    private ShotListener mShotListener;

    private TouchState mTouchState = new TouchState();
    private int mTouchAction = mTouchState.getTouchAction();
    public EnemyBoardPresenter(int boardSize, float turnBorderSize) {
        super(boardSize, turnBorderSize);
    }

    @Override
    public void measure(int w, int h, int horOffset, int verOffset, int smallestWidth) {
        super.measure(w, h, horOffset, verOffset, smallestWidth);
        mAnimationHorOffset = mBoardRect.left + mHalfCellSize;
        mAnimationVerOffset = mBoardRect.top + mHalfCellSize;
    }

    public void setShotListener(ShotListener shotListener) {
        mShotListener = shotListener;
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

    @NonNull
    public Rect getAimRectDst() {
        int x = mAim.getX();
        int y = mAim.getY();
        int left = x * mCellSizePx + mBoardRect.left;
        int top = y * mCellSizePx + mBoardRect.top;
        mLockDstRect.left = left;
        mLockDstRect.top = top;
        mLockDstRect.right = left + mCellSizePx;
        mLockDstRect.bottom = top + mCellSizePx;

        return mLockDstRect;
    }

    public int getTouchedCellY() {
        return mTouchY / mCellSizePx;
    }

    public int getTouchedCellX() {
        int x = -1;
        if (mTouchX > LEFT_MARGIN) {
            x = mTouchX / mCellSizePx;
        }

        return x;
    }

    public Rect getBoardRect() {
        return mBoardRect;
    }

    public boolean hasAim() {
        return mAim != null;
    }

    public void setAim(Vector2 aim) {
        mAim = aim;
    }

    public void removeAim() {
        mAim = null;
    }

    public void onTouch(TouchState touchState) {
        mTouchState = touchState;
        mTouchX = mTouchState.getTouchX();
        mTouchY = mTouchState.getTouchY();
        mTouchAction = mTouchState.getTouchAction();
        // TODO: create universal procedure to map x,y to cell
        if (mTouchAction == MotionEvent.ACTION_DOWN/* && !mLocked*/) {
            mShotListener.onAimingStarted();
        }

        if (mTouchAction == MotionEvent.ACTION_UP/* && !mLocked*/) {
            // TODO: unify these 2 callbacks
            mShotListener.onAimingFinished();
            mShotListener.onShot(getTouchedCellX(), getTouchedCellY());
        }
    }

    public boolean startedDragging() {
        return mTouchState.getDragStatus() == TouchState.START_DRAGGING;
    }

    public void unlock() {
        if (mTouchAction == MotionEvent.ACTION_DOWN || mTouchAction == MotionEvent.ACTION_MOVE) {
            mShotListener.onAimingStarted();
        }
    }
}
