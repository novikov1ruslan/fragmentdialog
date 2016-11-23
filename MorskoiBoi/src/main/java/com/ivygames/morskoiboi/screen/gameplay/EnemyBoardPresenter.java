package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.board.Coord;

import org.commons.logger.Ln;

final class EnemyBoardPresenter {

    private ShotListener mShotListener;

    private boolean mLocked = true;

    @NonNull
    private Coord mTouch = Coord.INVALID_VECTOR;
    private boolean mAimingStarted;
    private boolean mIsDragging;
    private int mLastTouchAction;

    void setShotListener(@NonNull ShotListener shotListener) {
        mShotListener = shotListener;
        Ln.v("listener: " + shotListener);
    }

    @Deprecated
    void touch(int action, int i, int j) {
        touch(action, Coord.get(i, j));
    }

    void touch(int action, @NonNull Coord v) {
        mTouch = v;
        mLastTouchAction = action;

        if (action == MotionEvent.ACTION_DOWN) {
            mIsDragging = true;
            if (!BoardUtils.contains(v)) {
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
                mShotListener.onAimingFinished(v.i, v.j);
            }
        }
    }

    void unlock() {
        mLocked = false;
        Ln.v("unlocked");
        if (isTouching(mLastTouchAction)) {
            if (BoardUtils.contains(mTouch)) {
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
