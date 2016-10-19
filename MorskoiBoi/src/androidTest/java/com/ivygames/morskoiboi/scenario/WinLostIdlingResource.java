package com.ivygames.morskoiboi.scenario;

import android.support.test.espresso.IdlingResource;
import android.util.Log;

class WinLostIdlingResource implements IdlingResource {
    private volatile ResourceCallback mCallback;
    private volatile boolean mIdle;

    @Override
    public String getName() {
        return WinLostIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if (mIdle) {
            mCallback.onTransitionToIdle();
        }
        Log.i("TEST", "idle=" + mIdle);
        return mIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    public void setIdle() {
        Log.i("TEST", "idle!");
        mIdle = true;
        mCallback.onTransitionToIdle();
    }
}
