package com.ivygames.morskoiboi.idlingresources;

import android.os.Handler;
import android.os.Looper;
import android.support.test.espresso.IdlingResource;


public class TaskResource implements IdlingResource {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean isRunning;

    public TaskResource(Runnable runnable) {
        run(runnable);
    }

    @Override
    public String getName() {
        return TaskResource.class.getSimpleName();
    }

    @Override
    public boolean isIdleNow() {
        return !isRunning;
    }

    @Override
    public void registerIdleTransitionCallback(final ResourceCallback callback) {
    }

    private void run(Runnable runnable) {
        isRunning = true;
        handler.post(runnable);
        handler.post(new Runnable() {
            @Override
            public void run() {
                isRunning = false;
            }
        });
    }
}
