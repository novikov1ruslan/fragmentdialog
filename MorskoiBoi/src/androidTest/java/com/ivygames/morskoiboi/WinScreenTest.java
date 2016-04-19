package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Opponent;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WinScreenTest extends ScreenTest {

    private static final String OPPONENT_NAME = "Sagi";
    private Collection<Ship> fleet = new ArrayList<>();
    private boolean surrendered;
    private Game game;
    private Rules rules;
    private AchievementsManager achievementsManager;
    private ProgressManager progressManager;
    private Opponent opponent;

    @Before
    public void setup() {
        super.setup();
        game = mock(Game.class);
        rules = mock(Rules.class);
        opponent = mock(Opponent.class);
        achievementsManager = mock(AchievementsManager.class);
        Dependencies.inject(achievementsManager);
        progressManager = mock(ProgressManager.class);
        Dependencies.inject(progressManager);

        Model.instance.game = game;
        Model.instance.opponent = opponent;
        RulesFactory.setRules(rules);
        when(opponent.getName()).thenReturn(OPPONENT_NAME);
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
        expectIncrementProgressBeCalled(times(1));
        verify(settings(), times(1)).setProgressPenalty(0);
    }

    @Test
    public void WhenScreenDisplayedWithNegativeScoreBalance__PenaltyUpdated() {
        setScores(100);
        setPenalty(200);
        showScreen();
        expectIncrementProgressBeCalled(never());
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
        checkDisplayed(withText(R.string.continue_str));
    }

    @Test
    public void AfterYesPressed__BoardSetupScreenShown() {
        WhenOpponentNotSurrendered__YesNoButtonsShowed();
        when(rules.getTotalShips()).thenReturn(new int[]{});
        clickOn(yesButton());
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void AfterNoPressedForAndroid__SelectGameScreenShown() {
        setGameType(Game.Type.VS_ANDROID);
        WhenOpponentNotSurrendered__YesNoButtonsShowed();
        clickOn(noButton());
        backToSelectGameCommand();
    }

    @Test
    public void AfterNoPressedForNonAndroid__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        WhenOpponentNotSurrendered__YesNoButtonsShowed();
        clickOn(noButton());
        String message = getString(R.string.want_to_leave_room, OPPONENT_NAME);
        checkDisplayed(withText(message));
    }

    @Test
    public void WhenOpponentSurrendersPressingBack__FinishesGameOpensSelectGameScreen() {
        surrendered = true;
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        backToSelectGameCommand();
    }

    @Test
    public void WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed() {
        surrendered = false;
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        String message = getString(R.string.want_to_leave_room, OPPONENT_NAME);
        checkDisplayed(withText(message));
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__RemovesDialogScreenDoesNotChange() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(cancelButton());
        checkDisplayed(WIN_LAYOUT);
        checkDoesNotExist(cancelButton());
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__SelectGameScreenDisplayed() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(withText(R.string.ok));
        backToSelectGameCommand();
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

    private void backToSelectGameCommand() {
        verify(game, times(1)).finish();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    private void expectSubmitScoreBeCalled(VerificationMode never) {
        verify(apiClient(), never).submitScore(anyString(), anyInt());
    }

    @NonNull
    private Matcher<View> signInBar() {
        return withId(R.id.sign_in_bar);
    }

    private void setGameType(Game.Type type) {
        when(game.getType()).thenReturn(type);
    }

    private void setScores(int scores) {
        setGameType(Game.Type.VS_ANDROID);
        when(rules.calcTotalScores(any(Collection.class), any(Game.class))).thenReturn(scores);
    }

    private void setPenalty(Integer penalty) {
        when(settings().getProgressPenalty()).thenReturn(penalty);
    }

    private void expectProcessAchievementsBeCalled(VerificationMode times) {
        verify(achievementsManager, times).processAchievements(any(Game.class), any(Collection.class));
    }

    private void expectIncrementProgressBeCalled(VerificationMode times) {
        verify(progressManager, times).incrementProgress(anyInt());
    }

    @NonNull
    private Matcher<View> timeView() {
        return withId(R.id.time);
    }

    @NonNull
    private Matcher<View> scoresView() {
        return withId(R.id.total_scores);
    }

    @NonNull
    protected Matcher<View> cancelButton() {
        return withText(R.string.cancel);
    }

    @NonNull
    protected Matcher<View> noButton() {
        return withText(R.string.no);
    }

    @NonNull
    protected Matcher<View> yesButton() {
        return withText(R.string.yes);
    }
}
