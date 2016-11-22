package com.ivygames.morskoiboi.win;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ScoresCalculator;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.config.RulesUtils;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.battleship.ship.Ship;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

abstract class WinScreen_ extends OnlineScreen_ {

    protected Rules rules;
    private AchievementsManager achievementsManager;
    // FIXME: substitute by real
    protected ProgressManager progressManager;
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
        return ViewMatchers.withId(R.id.time);
    }

    @NonNull
    protected Matcher<View> scoresView() {
        return withId(R.id.total_scores);
    }

    @NonNull
    protected Matcher<View> signInBar() {
        return withId(R.id.sign_in_bar);
    }

    protected void setScores(int scores) {
        setGameType(OnlineScreen_.Type.VS_ANDROID);
        when(RulesUtils.calcTotalScores(any(Collection.class), any(Game.class), any(ScoreStatistics.class),
                anyBoolean(), any(ScoresCalculator.class))).thenReturn(scores);
    }

    protected void setPenalty(Integer penalty) {
        when(settings().getProgressPenalty()).thenReturn(penalty);
    }
}
