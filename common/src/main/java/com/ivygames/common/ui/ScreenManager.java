package com.ivygames.common.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import static org.commons.logger.Ln.d;
import static org.commons.logger.Ln.e;
import static org.commons.logger.Ln.v;
import static org.commons.logger.Ln.w;

public class ScreenManager {

    @NonNull
    private final ViewGroup mContainer;

    private Screen mCurrentScreen;

    @Nullable
    private View mTutView;

    private boolean mStarted;
    private boolean mResumed;

    public ScreenManager(@NonNull ViewGroup container) {
        mContainer = container;
    }

    public void onStart() {
        mCurrentScreen.onStart();
        mStarted = true;
    }

    public void onResume() {
        if (!mStarted) {
            e("call start first");
        }
        mCurrentScreen.onResume();
        mResumed = true;
    }

    public void onPause() {
        if (!mResumed) {
            e("call resume first");
        }
        mCurrentScreen.onPause();
        mResumed = false;
    }

    public void onStop() {
        if (!mStarted) {
            e("call start first");
        }
        mCurrentScreen.onStop();
        mStarted = false;
    }

    public void onDestroy() {
        mCurrentScreen.onDestroy();
    }

    public void dismissTutorial() {
        if (mTutView == null) {
            v("no tutorial view to remove");
            return;
        }

        v("tutorial view present - removing");
        mContainer.removeView(mTutView);
        mTutView = null;
    }


    public void showTutorial(@Nullable View view) {
        if (mTutView == null) {
            mTutView = view;
            if (mTutView != null) {
                mContainer.addView(mTutView);
            }
        } else {
            d("tutorial view already shown: " + mTutView);
        }
    }

    public boolean handleBackPress() {
        v("top screen = " + mCurrentScreen);

        if (mTutView != null) {
            v("tutorial view present - removing");
            mContainer.removeView(mTutView);
            mTutView = null;
            return true;
        }

        if (mCurrentScreen instanceof BackPressListener) {
            v("propagating backpress");
            if (mCurrentScreen.isResumed()) {
                ((BackPressListener) mCurrentScreen).onBackPressed();
            } else {
                w("back pressed to fast for " + mCurrentScreen);
            }
            return true;
        }
        return false;
    }

    public void setScreen(@NonNull Screen screen) {
        View oldView = null;

        if (mCurrentScreen != null) {
            oldView = mCurrentScreen.getView();
            // TODO: unite pause/stop/destroy
            mCurrentScreen.onPause();
            mCurrentScreen.onStop();
            mCurrentScreen.onDestroy();
        }

        mCurrentScreen = screen;
        // TODO: rename onCreateView->onCreate
        mCurrentScreen.onCreateView(mContainer);
        View view = mCurrentScreen.getView();

        // TODO: change order of add and remove
        mContainer.addView(view);
        if (oldView != null) {
            mContainer.removeView(oldView);
        }

        if (mStarted) {
            mCurrentScreen.onStart();
            if (mResumed) {
                mCurrentScreen.onResume();
            }
        }
    }

    public boolean isStarted() {
        return mStarted;
    }

    public void onSignInSucceeded() {
        if (mCurrentScreen instanceof SignInListener) {
            ((SignInListener) mCurrentScreen).onSignInSucceeded();
        }
    }

}
