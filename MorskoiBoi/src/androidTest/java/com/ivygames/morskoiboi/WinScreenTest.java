package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WinScreenTest extends ScreenTest {

    private Collection<Ship> fleet = new ArrayList<>();
    private boolean surrendered;
    private Game game;
    private Rules rules;
    private AchievementsManager achievementsManager;
    private ProgressManager progressManager;

    @Before
    public void setup() {
        super.setup();
        game = mock(Game.class);
        rules = mock(Rules.class);
        achievementsManager = mock(AchievementsManager.class);
        Dependencies.inject(achievementsManager);
        progressManager = mock(ProgressManager.class);
        Dependencies.inject(progressManager);

        Model.instance.game = game;
        RulesFactory.setRules(rules);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new WinScreen(activity(), fleet, surrendered);
    }

    @Test
    public void WhenScreenDisplayed__GamesCounterIncremented() {
        setScreen(newScreen());
        verify(settings(), times(1)).incrementGamesPlayedCounter();
    }

    @Test
    public void WhenScreenDisplayedForAndroidGame__AchievementsProcessed() {
        setGameType(Game.Type.VS_ANDROID);
        setScreen(newScreen());
        verify(achievementsManager, times(1)).processAchievements(any(Game.class), any(Collection.class));
    }

    @Test
    public void WhenScreenDisplayed__ProgressUpdated() {
        setGameType(Game.Type.VS_ANDROID);
        setScores(100);
        setPenalty(0);
        setScreen(newScreen());
        verify(progressManager, times(1)).incrementProgress(anyInt());
        verify(settings(), times(1)).setProgressPenalty(0);
    }

    @Test
    public void WhenScreenDisplayed__PenaltyUpdated() {
        setGameType(Game.Type.VS_ANDROID);
        setScores(100);
        setPenalty(200);
        setScreen(newScreen());
        verify(progressManager, never()).incrementProgress(anyInt());
        verify(settings(), times(1)).setProgressPenalty(100);
    }

    @Test
    public void WhenGameTypeIsAndroid__ScoresAndDurationShown() {
        setGameType(Game.Type.VS_ANDROID);
        when(game.getTimeSpent()).thenReturn(135000L);
        setScores(100);
        setScreen(newScreen());
        onView(withId(R.id.time)).check(matches(withText("2:15")));
        onView(withId(R.id.total_scores)).check(matches(withText("100")));
    }

    @Test
    public void WhenSignedIn__SignInOptionHidden() {
        setGameType(Game.Type.VS_ANDROID);
        when(apiClient().isConnected()).thenReturn(true);
        setScreen(newScreen());
        checkNotDisplayed(signInBar());
    }

    @Test
    public void WhenNotAndroidGame__SignInOptionHidden() {
        setGameType(Game.Type.BLUETOOTH);
        when(apiClient().isConnected()).thenReturn(false);
        setScreen(newScreen());
        checkNotDisplayed(signInBar());
    }

    @Test
    public void WhenAndroidGameAndNotSignedIn__SignInOptionDisplayed() {
        setGameType(Game.Type.VS_ANDROID);
        when(apiClient().isConnected()).thenReturn(false);
        setScreen(newScreen());
        checkDisplayed(signInBar());
    }

    @Test
    public void WhenAndroidGameAndNotSignedIn__SignInOptionDisplayed2() {
        setGameType(Game.Type.VS_ANDROID);
        setSignedIn(false);
        setScreen(newScreen());
        onView(withId(R.id.sign_in_button)).perform(click());
        verify(apiClient(), times(1)).connect();
        signInSucceeded((SignInListener) screen());
        checkNotDisplayed(signInBar());
    }

    @Test
    public void WhenScreenDestroyedForAndroidConnectedGame__ScoresSubmitted() {
        setGameType(Game.Type.VS_ANDROID);
        setSignedIn(true);
        setScreen(newScreen());
        pressBack();
        verify(apiClient(), times(1)).submitScore(anyString(), anyInt());
    }

    @Test
    public void WhenOpponentSurrendersPressingBack__OpensSelectGameScreen() {
        surrendered = true;
        setScreen(newScreen());
        pressBack();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    @NonNull
    private Matcher<View> signInBar() {
        return withId(R.id.sign_in_bar);
    }

    private void setGameType(Game.Type type) {
        when(game.getType()).thenReturn(type);
    }

    private void setScores(int scores) {
        when(rules.calcTotalScores(any(Collection.class), any(Game.class))).thenReturn(scores);
    }

    private void setPenalty(Integer penalty) {
        when(settings().getProgressPenalty()).thenReturn(penalty);
    }
}
