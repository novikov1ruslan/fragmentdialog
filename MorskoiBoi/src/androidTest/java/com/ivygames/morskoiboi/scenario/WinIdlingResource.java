package com.ivygames.morskoiboi.scenario;

import android.support.test.espresso.IdlingResource;
import android.util.Log;

class WinIdlingResource implements IdlingResource {
    private ResourceCallback mCallback;
    private volatile boolean mWin;

    @Override
    public String getName() {
        return IdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if (mWin) {
            mCallback.onTransitionToIdle();
        }
        Log.v("TEST", "idle=" + mWin);
        return mWin;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    public void win() {
        mWin = true;
        if (mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }
}
