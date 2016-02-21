package com.ivygames.morskoiboi.screen.help;

import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;

import org.commons.logger.Ln;

public class HelpScreen extends BattleshipScreen implements BackPressListener {
    private static final String TAG = "HELP";
    private View mLayout;

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = inflate(R.layout.help, container);
        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onBackPressed() {
        mParent.setScreen(new MainScreen());
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

}
