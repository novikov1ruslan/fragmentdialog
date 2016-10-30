package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.screen.view.TouchState;

import org.commons.logger.Ln;

final class EnemyBoardPresenter {

    private ShotListener mShotListener;

    private boolean mLocked = true;

    @NonNull
    private TouchState mTouchState = new TouchState();

    void setShotListener(@NonNull ShotListener shotListener) {
        mShotListener = shotListener;
        Ln.v("listener: " + shotListener);
    }

    void touch(@NonNull TouchState event, int i, int j) {
        mTouchState = event;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            Ln.v("DOWN: x=" + event.getX() + "; y=" + event.getY());
            if (!mLocked) {
                mShotListener.onAimingStarted();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            Ln.v("UP: x=" + event.getX() + "; y=" + event.getY());
            if (!mLocked) {
                mShotListener.onAimingFinished(i, j);
            }
        }
    }

    void unlock() {
        mLocked = false;
        Ln.v("unlocked");
        int action = mTouchState.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mShotListener.onAimingStarted();
        }
    }

    void lock() {
        mLocked = true;
        Ln.v("locked");
    }

    boolean isLocked() {
        return mLocked;
    }

}
