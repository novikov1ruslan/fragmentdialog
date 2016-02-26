package com.ivygames.morskoiboi.screen;

import android.content.Intent;
import android.support.annotation.RawRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.MusicPlayer;
import com.ivygames.morskoiboi.analytics.UiEvent;

import org.acra.ACRA;
import org.commons.logger.Ln;

public abstract class BattleshipScreen extends Screen {

    // TODO: make these final, remove onAttach() method
    protected GoogleApiClient mApiClient;
    protected FragmentManager mFm;

    private boolean mResumed;

    public final void onAttach(BattleshipActivity activity) {
        mParent = activity;
        mApiClient = mParent.getApiClient();
        UiEvent.screenView(this.getClass().getSimpleName());
        mFm = getFragmentManager();
        Ln.v(this + " attached");
    }

    public void onCreate() {
        Ln.v(this + " creating");
    }

    public abstract View onCreateView(ViewGroup container);

    public void onStart() {
        Ln.v(this + " started");
    }

    public void onDestroyView() {
        Ln.v(this + " screen view destroyed");
    }

    public void onDestroy() {
        Ln.v(this + " screen destroyed");
    }

    protected final void setScreen(BattleshipScreen screen) {
        mParent.setScreen(screen);
    }

    protected final String debugSuffix() {
        return "(" + (hashCode() % 1000) + ")";
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BattleshipActivity.RC_ENABLE_BT) {
            Ln.w("unprocessed BT result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        } else if (requestCode == BattleshipActivity.RC_ENSURE_DISCOVERABLE) {
            Ln.w("unprocessed BT result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        } else {
            Ln.e("unprocessed result=" + resultCode + ", request=" + requestCode + ", data=" + data);
            ACRA.getErrorReporter().handleException(new RuntimeException(this + " unprocessed result: " + resultCode));
        }
    }

    public View getTutView() {
        return null;
    }

    public @RawRes int getMusic() {
        return MusicPlayer.NO_SOUND;
    }
}