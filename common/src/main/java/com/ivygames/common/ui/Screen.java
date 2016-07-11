package com.ivygames.common.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.commons.logger.Ln;

public abstract class Screen {

    @NonNull
    protected final Activity mParent;
    private boolean mResumed;

    protected Screen(@NonNull Activity parent) {
        mParent = parent;
    }

    @NonNull
    protected final FragmentManager getFragmentManager() {
        return mParent.getFragmentManager();
    }

    @NonNull
    public abstract View getView();

    protected final View inflate(int layoutId) {
        return mParent.getLayoutInflater().inflate(layoutId, null);
    }

    protected final String getString(int resId) {
        return mParent.getString(resId);
    }

    protected final String getString(int resId, Object... formatArgs) {
        return mParent.getString(resId, formatArgs);
    }

    protected final void startActivity(Intent intent) {
        mParent.startActivity(intent);
    }

    protected final void startActivityForResult(Intent intent, int requestCode) {
        mParent.startActivityForResult(intent, requestCode);
    }

    protected final LayoutInflater getLayoutInflater() {
        return mParent.getLayoutInflater();
    }

    protected final View inflate(int resId, ViewGroup container) {
        return getLayoutInflater().inflate(resId, container, false);
    }

    protected final Resources getResources() {
        return mParent.getResources();
    }

    public abstract View onCreateView(@NonNull ViewGroup container);

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
