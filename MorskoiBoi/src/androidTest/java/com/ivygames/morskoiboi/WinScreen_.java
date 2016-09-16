package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.mockito.verification.VerificationMode;

import java.util.ArrayList;
import java.util.Collection;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WinScreen_ extends OnlineScreen_ {

    protected Rules rules;
    private AchievementsManager achievementsManager;
    private ProgressManager progressManager;
    private Collection<Ship> fleet = new ArrayList<>();
    protected boolean surrendered;
    protected ScoreStatistics statistics;

    @Before
    public void setup() {
        super.setup();
        rules = mock(Rules.class);
        achievementsManager = mock(AchievementsManager.class);
        Dependencies.inject(achievementsManager);
        progressManager = mock(ProgressManager.class);
        Dependencies.inject(progressManager);
        statistics = mock(ScoreStatistics.class);

        when(settings().incrementProgress(anyInt())).thenReturn(new Progress(0));
        Dependencies.inject(rules);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new WinScreen(activity, game, session, fleet, statistics, surrendered);
    }

    protected void expectProcessAchievementsBeCalled(VerificationMode times) {
        verify(achievementsManager, times).processScores(anyInt());
    }

    protected void expectSynchronizeBeCalled(VerificationMode times) {
        verify(progressManager, times).synchronize();
    }

    @NonNull
    protected Matcher<View> timeView() {
        return withId(R.id.time);
    }

    @NonNull
    protected Matcher<View> scoresView() {
        return withId(R.id.total_scores);
    }

    protected void expectSubmitScoreBeCalled(VerificationMode never) {
        verify(apiClient(), never).submitScore(anyString(), anyInt());
    }

    @NonNull
    protected Matcher<View> signInBar() {
        return withId(R.id.sign_in_bar);
    }

    protected void setScores(int scores) {
        setGameType(Game.Type.VS_ANDROID);
        when(rules.calcTotalScores(any(Collection.class), any(Game.Type.class), any(ScoreStatistics.class), anyBoolean())).thenReturn(scores);
    }

    protected void setPenalty(Integer penalty) {
        when(settings().getProgressPenalty()).thenReturn(penalty);
    }
}
