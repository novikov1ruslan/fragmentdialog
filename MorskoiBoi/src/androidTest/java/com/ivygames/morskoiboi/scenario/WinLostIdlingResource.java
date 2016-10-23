package com.ivygames.morskoiboi.scenario;

import android.support.test.espresso.IdlingResource;
import android.util.Log;

class WinLostIdlingResource implements IdlingResource {
    private volatile ResourceCallback mCallback;
    private volatile boolean mIdle = true;

    @Override
    public String getName() {
        return WinLostIdlingResource.class.getName() + hashCode();
    }

    @Override
    public boolean isIdleNow() {
//        if (mIdle) {
//            mCallback.onTransitionToIdle();
//        }
        log("idle=" + mIdle);
        return mIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    public void setIdle(boolean idle) {
        log("setting idle=" + idle);
        mIdle = idle;
        if (mIdle && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }

    private void log(String msg) {
        Log.i("TEST", msg);
    }
}
