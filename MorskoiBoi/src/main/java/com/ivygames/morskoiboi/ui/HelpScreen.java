package com.ivygames.morskoiboi.ui;

import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;

import org.commons.logger.Ln;

public class HelpScreen extends BattleshipScreen implements BackPressListener {
    private static final String TAG = "HELP";
    private View mLayout;

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = getLayoutInflater().inflate(R.layout.help, container, false);
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
    public String toString() {
        return TAG + debugSuffix();
    }

}
