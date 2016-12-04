package com.ivygames.morskoiboi.win;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.battleship.Rules;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ScoreStatistics;
import com.ivygames.morskoiboi.ScoresCalculator;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class WinScreen_ extends OnlineScreen_ {

    protected Rules rules;
    private AchievementsManager achievementsManager;
    // FIXME: substitute by real
    private ProgressManager progressManager;
    private Collection<Ship> fleet = new ArrayList<>();
    boolean surrendered;
    ScoreStatistics statistics;
    @Mock
    ScoresCalculator scoresCalculator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        super.setup();

        rules = mock(Rules.class);
        achievementsManager = mock(AchievementsManager.class);
        Dependencies.inject(achievementsManager);
        progressManager = mock(ProgressManager.class);
        Dependencies.inject(progressManager);
        statistics = mock(ScoreStatistics.class);

        when(settings().incrementProgress(anyInt())).thenReturn(0);
        Dependencies.inject(rules);

        Dependencies.inject(scoresCalculator);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new WinScreen(activity, game, session, fleet, statistics, surrendered);
    }

    @NonNull
    Matcher<View> timeView() {
        return ViewMatchers.withId(R.id.time);
    }

    @NonNull
    Matcher<View> scoresView() {
        return withId(R.id.total_scores);
    }

    @NonNull
    Matcher<View> signInBar() {
        return withId(R.id.sign_in_bar);
    }

}
