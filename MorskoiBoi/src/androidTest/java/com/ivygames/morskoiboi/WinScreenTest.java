package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import java.util.ArrayList;
import java.util.Collection;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WinScreenTest extends OnlineScreen_ {

    private Collection<Ship> fleet = new ArrayList<>();
    protected boolean surrendered;
    private Rules rules;
    private AchievementsManager achievementsManager;
    private ProgressManager progressManager;

    @Before
    public void setup() {
        super.setup();
        rules = mock(Rules.class);
        achievementsManager = mock(AchievementsManager.class);
        Dependencies.inject(achievementsManager);
        progressManager = mock(ProgressManager.class);
        Dependencies.inject(progressManager);

        when(settings().incrementProgress(anyInt())).thenReturn(new Progress(0));
        RulesFactory.setRules(rules);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new WinScreen(activity(), fleet, surrendered);
    }

    @Test
    public void WhenScreenDisplayed__GamesCounterIncremented() {
        showScreen();
        verify(settings(), times(1)).incrementGamesPlayedCounter();
    }

    @Test
    public void WhenScreenDisplayedForAndroidGame__AchievementsProcessed() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        expectProcessAchievementsBeCalled(times(1));
    }

    @Test
    public void WhenScreenDisplayedForNonAndroidGame__AchievementsNotProcessed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        expectProcessAchievementsBeCalled(never());
    }

    @Test
    public void WhenScreenDisplayedWithPositiveScoreBalance__ProgressUpdated() {
        setScores(100);
        setPenalty(0);
        showScreen();
        expectUpdateProgressBeCalled(times(1));
        verify(settings(), times(1)).incrementProgress(100);
        verify(settings(), times(1)).setProgressPenalty(0);
    }

    @Test
    public void WhenScreenDisplayedWithNegativeScoreBalance__PenaltyUpdated() {
        setScores(100);
        setPenalty(200);
        showScreen();
        expectUpdateProgressBeCalled(never());
        verify(settings(), never()).incrementProgress(anyInt());
        verify(settings(), times(1)).setProgressPenalty(100);
    }

    @Test
    public void WhenGameTypeIsAndroid__ScoresAndDurationShown() {
        setGameType(Game.Type.VS_ANDROID);
        when(game.getTimeSpent()).thenReturn(135000L);
        setScores(100);
        showScreen();
        onView(timeView()).check(matches(withText("2:15")));
        onView(scoresView()).check(matches(withText("100")));
    }

    @Test
    public void WhenGameTypeIsNotAndroid__ScoresAndDurationNotShown() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        checkNotDisplayed(timeView());
        checkNotDisplayed(scoresView());
    }

    @Test
    public void WhenSignedIn__SignInOptionHidden() {
        setGameType(Game.Type.VS_ANDROID);
        setSignedIn(true);
        showScreen();
        checkNotDisplayed(signInBar());
    }

    @Test
    public void WhenNotAndroidGame__SignInOptionHidden() {
        setGameType(Game.Type.BLUETOOTH);
        setSignedIn(false);
        showScreen();
        checkNotDisplayed(signInBar());
    }

    @Test
    public void WhenAndroidGameAndNotSignedIn__SignInOptionDisplayed() {
        setGameType(Game.Type.VS_ANDROID);
        setSignedIn(false);
        showScreen();
        checkDisplayed(signInBar());
    }

    @Test
    public void AfterSignInClicked__SignInOptionHidden() {
        WhenAndroidGameAndNotSignedIn__SignInOptionDisplayed();
        clickOn(withId(R.id.sign_in_button));
        verify(apiClient(), times(1)).connect();
        signInSucceeded((SignInListener) screen());
        checkNotDisplayed(signInBar());
    }

    @Test
    public void WhenOpponentNotSurrendered__YesNoButtonsShowed() {
        surrendered = false;
        showScreen();
        checkDisplayed(yesButton());
        checkDisplayed(noButton());
    }

    @Test
    public void WhenOpponentSurrendered__InsteadOfYesNoContinueButtonShowed() {
        surrendered = true;
        showScreen();
        checkNotDisplayed(yesButton());
        checkNotDisplayed(noButton());
        checkDisplayed(continueButton());
    }

    @Test
    public void AfterYesPressed__BoardSetupScreenShown() {
        WhenOpponentNotSurrendered__YesNoButtonsShowed();
        when(rules.getAllShipsSizes()).thenReturn(new int[]{});
        clickOn(yesButton());
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void AfterNoPressedForAndroid__SelectGameScreenShown() {
        setGameType(Game.Type.VS_ANDROID);
        WhenOpponentNotSurrendered__YesNoButtonsShowed();
        clickOn(noButton());
        FinishGame_BackToSelectGame();
    }

    @Test
    public void WhenOpponentSurrendersPressingBack__FinishesGameOpensSelectGameScreen() {
        surrendered = true;
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        FinishGame_BackToSelectGame();
    }

    @Test
    public void WhenScreenDestroyedForAndroidConnectedGame__ScoresSubmitted() {
        setGameType(Game.Type.VS_ANDROID);
        setSignedIn(true);
        showScreen();
        pressBack();
        expectSubmitScoreBeCalled(times(1));
    }

    @Test
    public void WhenScreenDestroyedForNonAndroidGame__ScoresNotSubmitted() {
        setGameType(Game.Type.BLUETOOTH);
        setSignedIn(true);
        showScreen();
        pressBack();
        expectSubmitScoreBeCalled(never());
    }

    @Test
    public void WhenScreenDestroyedWhenNotConnected__ScoresNotSubmitted() {
        setGameType(Game.Type.VS_ANDROID);
        setSignedIn(false);
        showScreen();
        pressBack();
        expectSubmitScoreBeCalled(never());
    }

    private void expectSubmitScoreBeCalled(VerificationMode never) {
        verify(apiClient(), never).submitScore(anyString(), anyInt());
    }

    @NonNull
    private Matcher<View> signInBar() {
        return withId(R.id.sign_in_bar);
    }

    private void setScores(int scores) {
        setGameType(Game.Type.VS_ANDROID);
        when(rules.calcTotalScores(any(Collection.class), any(Game.class), anyBoolean())).thenReturn(scores);
    }

    private void setPenalty(Integer penalty) {
        when(settings().getProgressPenalty()).thenReturn(penalty);
    }

    private void expectProcessAchievementsBeCalled(VerificationMode times) {
        verify(achievementsManager, times).processAchievements(any(Game.class), any(Collection.class), anyInt());
    }

    private void expectUpdateProgressBeCalled(VerificationMode times) {
        verify(progressManager, times).updateProgress(any(Progress.class));
    }

    @NonNull
    private Matcher<View> timeView() {
        return withId(R.id.time);
    }

    @NonNull
    private Matcher<View> scoresView() {
        return withId(R.id.total_scores);
    }

}
