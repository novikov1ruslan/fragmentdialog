package com.ivygames.common;

import android.support.annotation.NonNull;

public abstract class Command implements Runnable {

    private boolean mExecuted;
    private Runnable mNextCommand;

    protected abstract void execute();

    @Override
    public final void run() {
        execute();
        mExecuted = true;
        if (mNextCommand != null) {
            mNextCommand.run();
        }
    }

    public boolean executed() {
        return mExecuted;
    }

    public void setNextCommand(@NonNull Runnable nextCommand) {
        mNextCommand = nextCommand;
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }

}
