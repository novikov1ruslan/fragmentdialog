package com.ivygames.morskoiboi.screen.ranks;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.DebugUtils;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;

import org.commons.logger.Ln;

public class RanksListScreen extends BattleshipScreen implements BackPressListener {
    private RanksLayout mLayout;

    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();

    public RanksListScreen(@NonNull BattleshipActivity parent) {
        super(parent);
    }

    private static void debug_setProgress(int progress, GameSettings settings) {
        Ln.i("setting debug progress to: " + progress);
        settings.setProgress(progress);
        Dependencies.getProgressManager().synchronize();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (RanksLayout) inflate(R.layout.ranks_list, container);
        mLayout.setTotalScore(mSettings.getProgress());

        mLayout.debug_setDebugListener(new RanksLayout.DebugListener() {
            @Override
            public void onDebugScoreSet(int score) {
                debug_setProgress(score, mSettings);
            }
        });

        Ln.d(this + " screen created");
        return mLayout;
    }

    @NonNull
    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onBackPressed() {
        setScreen(ScreenCreator.newSelectGameScreen());
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
