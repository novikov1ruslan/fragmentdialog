package com.ivygames.morskoiboi.idlingresources;

import android.os.Handler;
import android.os.Looper;
import android.support.test.espresso.IdlingResource;


public class TaskResource implements IdlingResource {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean isTransitionRunning;
    private volatile ResourceCallback resourceCallback;

    public TaskResource(Runnable runnable) {
        runTransition(runnable);
    }

    @Override
    public String getName() {
        return TaskResource.class.getSimpleName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !isTransitionRunning;
        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(final ResourceCallback callback) {
        resourceCallback = callback;
    }

    private void runTransition(Runnable runnable) {
        isTransitionRunning = true;
        handler.post(runnable);
        handler.post(new Runnable() {
            @Override
            public void run() {
                isTransitionRunning = false;
            }
        });
    }
}
