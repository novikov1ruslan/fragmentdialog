package com.ivygames.morskoiboi.screen;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.music.MusicPlayer;
import com.ivygames.common.ui.ActivityScreen;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.RequestCodes;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public abstract class BattleshipScreen extends ActivityScreen {

    @NonNull
    protected final FragmentManager mFm;

    protected BattleshipScreen(BattleshipActivity parent) {
        super(parent);
        mFm = getFragmentManager();
        UiEvent.screenView(this.getClass().getSimpleName());
    }

    @Override
    public void onAttach() {

    }

    protected final void setScreen(BattleshipScreen screen) {
        parent().setScreen(screen);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.RC_ENABLE_BT) {
            Ln.w("unprocessed BT result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        } else if (requestCode == RequestCodes.RC_ENSURE_DISCOVERABLE) {
            Ln.w("unprocessed BT result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        } else if (requestCode == RequestCodes.PLUS_ONE_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            Ln.i("+1 request cancelled");
        } else {
            Ln.w("unprocessed result=" + resultCode + ", request=" + RequestCodes.getDebugName(requestCode) + ", data=" + data);
            reportException(this + " unprocessed result: " + resultCode);
        }
    }

    @RawRes
    public int getMusic() {
        return MusicPlayer.NO_SOUND;
    }

    protected final BattleshipActivity parent() {
        return (BattleshipActivity) mParent;
    }
}
