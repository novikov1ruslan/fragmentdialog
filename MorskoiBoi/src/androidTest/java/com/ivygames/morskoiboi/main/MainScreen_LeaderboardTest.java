package com.ivygames.morskoiboi.main;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.morskoiboi.R;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static org.mockito.Mockito.never;

public class MainScreen_LeaderboardTest extends MainScreen_ {
    @NonNull
    protected static Matcher<View> leaderboardButton() {
        return ViewMatchers.withId(R.id.high_score);
    }

    @NonNull
    protected static Matcher<View> leaderBoardDialog() {
        return withText(R.string.leaderboards_request);
    }

    @Test
    public void WhenLeaderBoardPressedAndNotConnected__SignInDialogDisplayed() {
        setSignedIn(false);
        showScreen();
        onView(leaderboardButton()).perform(click());
        onView(leaderBoardDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void WhenSignInPressedForLeaderBoardDialog__Connected() {
        WhenLeaderBoardPressedAndNotConnected__SignInDialogDisplayed();
        clickOn(signInButton());
        verifyConnected();
    }

    @Test
    public void WhenCancelPressedForSignInDialog__NotConnectedAndDialogDismissed() {
        WhenLeaderBoardPressedAndNotConnected__SignInDialogDisplayed();
        clickOn(cancelButton());
        verifyConnected(never());
        checkDoesNotExist(leaderBoardDialog());
    }

    @Test
    public void WhenBackPressedForLeaderBoardDialog__DialogDismissed() {
        WhenLeaderBoardPressedAndNotConnected__SignInDialogDisplayed();
        pressBack();
        checkDoesNotExist(leaderBoardDialog());
    }

    @Test
    public void when_leader_board_button_is_pressed_when_signed_in__leader_board_intent_is_fired() {
        setSignedIn(true);
        showScreen();

        clickOn(leaderboardButton());

        verifyLeaderboardsShown();
    }

}
