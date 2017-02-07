package com.ivygames.common.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.commons.logger.LoggerUtils;


public abstract class ActivityScreen extends Screen {
    @NonNull
    protected final Activity mParent;

    @NonNull
    protected final FragmentManager getFragmentManager() {
        return mParent.getFragmentManager();
    }

    protected final View inflate(int layoutId) {
        return mParent.getLayoutInflater().inflate(layoutId, null);
    }

    protected final String getString(int resId) {
        return mParent.getString(resId);
    }

    protected final String getString(int resId, @NonNull Object... formatArgs) {
        return mParent.getString(resId, formatArgs);
    }

    protected final void startActivity(@NonNull Intent intent) {
        mParent.startActivity(intent);
    }

    protected final void startActivityForResult(@NonNull Intent intent, int requestCode) {
        mParent.startActivityForResult(intent, requestCode);
    }

    protected final LayoutInflater getLayoutInflater() {
        return mParent.getLayoutInflater();
    }

    protected final View inflate(int resId, @NonNull ViewGroup container) {
        return getLayoutInflater().inflate(resId, container, false);
    }

    @NonNull
    protected final Resources getResources() {
        return mParent.getResources();
    }

    protected ActivityScreen(@NonNull Activity parent) {
        mParent = parent;
    }

    @Override
    public String toString() {
        return LoggerUtils.getSimpleName(this);
    }
}
