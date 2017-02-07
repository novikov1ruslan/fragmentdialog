package com.ivygames.common.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import org.commons.logger.Ln;

public abstract class Screen {

    private boolean mResumed;

    @NonNull
    public abstract View onCreateView(@NonNull ViewGroup container);

    public abstract void onAttach();

    @NonNull
    public abstract View getView();

    public void onStart() {
        Ln.v(this + " started");
    }

    public void onDestroy() {
        Ln.v(this + " screen destroyed");
    }

    public void onStop() {
        Ln.v(this + " stopped");
    }

    public boolean isResumed() {
        return mResumed;
    }

    public void onPause() {
        mResumed = false;
        Ln.v(this + " paused");
    }

    public void onResume() {
        mResumed = true;
        Ln.v(this + " resumed");
    }

    @Nullable
    public View getTutView() {
        return null;
    }

}
