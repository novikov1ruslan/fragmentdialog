package com.ivygames.morskoiboi.screen;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.analytics.UiEvent;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.MusicPlayer;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;
import static org.apache.commons.lang3.Validate.notNull;

// TODO: Screen is never used in isolation - combine BattleshipScreen with Screen
public abstract class BattleshipScreen extends Screen {

    @NonNull
    protected final FragmentManager mFm;

    private boolean mResumed;

    public BattleshipScreen(BattleshipActivity parent) {
        super(parent);
        mFm = notNull(getFragmentManager());
        UiEvent.screenView(this.getClass().getSimpleName());
        Ln.v(this + " created");
    }

    public abstract View onCreateView(ViewGroup container);

    public void onStart() {
        Ln.v(this + " started");
    }

    public void onDestroy() {
        Ln.v(this + " screen destroyed");
    }

    protected final void setScreen(BattleshipScreen screen) {
        parent().setScreen(screen);
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
            reportException(this + " unprocessed result: " + resultCode);
        }
    }

    public View getTutView() {
        return null;
    }

    public @RawRes int getMusic() {
        return MusicPlayer.NO_SOUND;
    }

    public final BattleshipActivity parent() {
        return (BattleshipActivity) mParent;
    }
}
