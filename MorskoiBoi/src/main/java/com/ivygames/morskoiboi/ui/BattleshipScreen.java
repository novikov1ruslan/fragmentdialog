package com.ivygames.morskoiboi.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ivygames.morskoiboi.R;

import org.acra.ACRA;
import org.commons.logger.Ln;

public abstract class BattleshipScreen extends Screen {

    protected Tracker mGaTracker;
    protected GoogleApiClient mApiClient;
    protected FragmentManager mFm;

    private WaitFragment mWaitFragment;
    private boolean mResumed;
    private boolean mStarted;

    public void onAttach(BattleshipActivity activity) {
        mParent = activity;
        mApiClient = mParent.getApiClient();
        mGaTracker = mParent.getTracker();
    }

    public void onCreate() {
        Ln.v(this + " fragment creating");

        mFm = mParent.getSupportFragmentManager();
    }

    public abstract View onCreateView(ViewGroup container);

    public void onStart() {
        mGaTracker.setScreenName(this.getClass().getSimpleName());
        mGaTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mStarted = true;
    }

    public void onDestroyView() {
        Ln.v(this + " fragment view destroyed");
    }

    public void onDestroy() {
        Ln.v(this + " fragment destroyed");
    }

    protected final void showWaitingScreen() {
        Ln.d("please wait... ");
        mWaitFragment = new WaitFragment();
        mFm.beginTransaction().add(R.id.container, mWaitFragment).commitAllowingStateLoss();
    }

    protected final void hideWaitingScreen() {
        if (isWaitShown()) {
            Ln.d("hiding waiting screen");
            mFm.beginTransaction().remove(mWaitFragment).commitAllowingStateLoss();
            mWaitFragment = null;
        } else {
            Ln.w("waiting screen isn't shown");
        }
    }

    protected final boolean isWaitShown() {
        return mWaitFragment != null;
    }

    protected final String debugSuffix() {
        return "(" + (hashCode() % 1000) + ")";
    }

    public void onStop() {
        mStarted = false;
    }

    public boolean isResumed() {
        return mResumed;
    }

    public void onPause() {
        mResumed = false;
    }

    public void onResume() {
        mResumed = true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Ln.e("unprocessed result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        ACRA.getErrorReporter().handleException(new RuntimeException("unprocessed result: " + resultCode));
    }

    public View getTutView() {
        return null;
    }
}
