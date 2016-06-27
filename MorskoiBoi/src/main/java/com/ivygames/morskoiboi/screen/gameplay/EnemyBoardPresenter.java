package com.ivygames.morskoiboi.screen.gameplay;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.BasePresenter;
import com.ivygames.morskoiboi.screen.view.TouchState;

import org.commons.logger.Ln;

final class EnemyBoardPresenter extends BasePresenter {

    private int mAnimationHorOffset;
    private int mAnimationVerOffset;
    @NonNull
    private final Rect mDstRect = new Rect();
    @NonNull
    private final Rect mLockDstRect = new Rect();

    private ShotListener mShotListener;

    @NonNull
    private final TouchState mTouchState = new TouchState();

    private boolean mLocked = true;

    public EnemyBoardPresenter(int boardSize, float turnBorderSize) {
        super(boardSize, turnBorderSize);
    }

    @Override
    public void measure(int w, int h, int hPadding, int vPadding) {
        super.measure(w, h, hPadding, vPadding);
        mAnimationVerOffset = mBoardRect.top + mHalfCellSize;
        mAnimationHorOffset = mBoardRect.left + mHalfCellSize;
    }

    @Override
    protected void setBoardVerticalOffset(int offset) {
        super.setBoardVerticalOffset(offset);
        mAnimationVerOffset = mBoardRect.top + mHalfCellSize;
    }

    public void setShotListener(@NonNull ShotListener shotListener) {
        mShotListener = shotListener;
    }

    @NonNull
    public Rect getAnimationDestination(@NonNull Vector2 aim, float cellRatio) {
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
    public Rect getAimRectDst(@NonNull Vector2 aim) {
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

    public int getTouchedJ() {
        int y = mTouchState.getY() - mBoardRect.top;
        return y / mCellSizePx;
    }

    public int getTouchedI() {
        int x = mTouchState.getX() - mBoardRect.left;
        return x / mCellSizePx;
    }

    @NonNull
    public Rect getBoardRect() {
        return mBoardRect;
    }

    public void touch(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        int action = mTouchState.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            Ln.v("DOWN: x=" + mTouchState.getX() + "; y=" + mTouchState.getY());
            if (!mLocked) {
                mShotListener.onAimingStarted();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            Ln.v("UP: x=" + mTouchState.getX() + "; y=" + mTouchState.getY());
            if (!mLocked) {
                mShotListener.onAimingFinished(getTouchedI(), getTouchedJ());
            }
        }
    }

    public boolean startedDragging() {
        return mTouchState.getDragStatus() == TouchState.START_DRAGGING;
    }

    public void unlock() {
        mLocked = false;
        Ln.v("unlocked");
        int action = mTouchState.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mShotListener.onAimingStarted();
        }
    }

    public void lock() {
        mLocked = true;
        Ln.v("locked");
    }

    public boolean isLocked() {
        return mLocked;
    }

}
