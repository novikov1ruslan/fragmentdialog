package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.TouchState;

import org.commons.logger.Ln;

final class EnemyBoardPresenter {

    private ShotListener mShotListener;

    private boolean mLocked = true;

    @NonNull
    private TouchState mTouchState = new TouchState();
    @NonNull
    private Vector2 mTouch = Vector2.INVALID_VECTOR;
    private boolean mAimingStarted;

    void setShotListener(@NonNull ShotListener shotListener) {
        mShotListener = shotListener;
        Ln.v("listener: " + shotListener);
    }

    @Deprecated
    void touch(@NonNull TouchState event, int i, int j) {
        touch(event, Vector2.get(i, j));
    }

    void touch(@NonNull TouchState event, @NonNull Vector2 v) {
        mTouchState = event;
        mTouch = v;

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (!Board.contains(v)) {
                Ln.w("pressing outside the board: " + v);
                return;
            }

            Ln.v("DOWN: x=" + event.getX() + "; y=" + event.getY());
            if (!mLocked) {
                mAimingStarted = true;
                mShotListener.onAimingStarted();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            Ln.v("UP: x=" + event.getX() + "; y=" + event.getY());
            if (mAimingStarted) {
                mAimingStarted = false;
                mShotListener.onAimingFinished(v.getX(), v.getY());
            }
        }
    }

    void unlock() {
        mLocked = false;
        Ln.v("unlocked");
        if (isTouching(mTouchState.getAction())) {
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
}
