package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

final class EnemyBoardPresenter {

    private ShotListener mShotListener;

    private boolean mLocked = true;

    @NonNull
    private Vector2 mTouch = Vector2.INVALID_VECTOR;
    private boolean mAimingStarted;
    private boolean mIsDragging;
    private int mLastTouchAction;

    void setShotListener(@NonNull ShotListener shotListener) {
        mShotListener = shotListener;
        Ln.v("listener: " + shotListener);
    }

    @Deprecated
    void touch(int action, int i, int j) {
        touch(action, Vector2.get(i, j));
    }

    void touch(int action, @NonNull Vector2 v) {
        mTouch = v;
        mLastTouchAction = action;

        if (action == MotionEvent.ACTION_DOWN) {
            mIsDragging = true;
            if (!Board.contains(v)) {
                Ln.w("pressing outside the board: " + v);
                return;
            }

            if (!mLocked) {
                mAimingStarted = true;
                mShotListener.onAimingStarted();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            mIsDragging = false;
            if (mAimingStarted) {
                mAimingStarted = false;
                mShotListener.onAimingFinished(v.getX(), v.getY());
            }
        }
    }

    void unlock() {
        mLocked = false;
        Ln.v("unlocked");
        if (isTouching(mLastTouchAction)) {
            if (Board.contains(mTouch)) {
                mAimingStarted = true;
                mShotListener.onAimingStarted();
            } else {
                Ln.w("unlocked when not started");
            }
        }
    }

    private boolean isTouching(int action) {
        return action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE;
    }

    void lock() {
        mLocked = true;
        Ln.v("locked");
    }

    boolean isLocked() {
        return mLocked;
    }

    public boolean isDragging() {
        return mIsDragging;
    }
}
