package com.ivygames.morskoiboi.screen.ranks;

import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;

import org.commons.logger.Ln;

public class RanksListScreen extends BattleshipScreen implements BackPressListener {
    private static final String TAG = "RANKS";
    private RanksLayout mLayout;

    public RanksListScreen(BattleshipActivity parent) {
        super(parent);
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (RanksLayout) inflate(R.layout.ranks_list, container);
        mLayout.setTotalScore(GameSettings.get().getProgress().getScores());

        mLayout.debug_setDebugListener(new RanksLayout.DebugListener() {
            @Override
            public void onDebugScoreSet(int score) {
                new ProgressManager(mApiClient).debug_setProgress(score);
            }
        });

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onBackPressed() {
        mParent.setScreen(new SelectGameScreen(getParent()));
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
