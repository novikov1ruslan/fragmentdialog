package com.ivygames.morskoiboi.screen.ranks;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameHandler;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;

import org.commons.logger.Ln;

public class RanksListScreen extends BattleshipScreen implements BackPressListener {
    private static final String TAG = "RANKS";
    private RanksLayout mLayout;

    @NonNull
    private final GoogleApiClientWrapper mApiClient;

    @NonNull
    private final GameSettings mSettings;

    public RanksListScreen(@NonNull BattleshipActivity parent, @NonNull GameSettings settings) {
        super(parent);
        mSettings = settings;
        mApiClient = Dependencies.getApiClient();
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (RanksLayout) inflate(R.layout.ranks_list, container);
        mLayout.setTotalScore(mSettings.getProgress().getScores());

        mLayout.debug_setDebugListener(new RanksLayout.DebugListener() {
            @Override
            public void onDebugScoreSet(int score) {
                new ProgressManager(mApiClient, mSettings).debug_setProgress(score);
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
        setScreen(GameHandler.newSelectGameScreen());
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
