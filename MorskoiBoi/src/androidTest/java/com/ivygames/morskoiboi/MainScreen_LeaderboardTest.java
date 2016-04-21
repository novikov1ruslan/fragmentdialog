package com.ivygames.morskoiboi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainScreen_LeaderboardTest extends MainScreenTest {
    @NonNull
    protected static Matcher<View> leaderboardButton() {
        return withId(R.id.high_score);
    }

    @NonNull
    protected static Matcher<View> leaderBoardDialog() {
        return withText(R.string.leaderboards_request);
    }

    @Test
    public void WhenLeaderBoardPressedAndNotConnected__SignInDialogDisplayed() {
        setSignedIn(false);
        setScreen(newScreen());
        onView(leaderboardButton()).perform(click());
        onView(leaderBoardDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void WhenSignInPressedForLeaderBoardDialog__Connected() {
        WhenLeaderBoardPressedAndNotConnected__SignInDialogDisplayed();
        clickOn(signInButton());
        verify(apiClient(), times(1)).connect();
    }

    @Test
    public void WhenCancelPressedForSignInDialog__NotConnectedAndDialogDismissed() {
        WhenLeaderBoardPressedAndNotConnected__SignInDialogDisplayed();
        clickOn(cancelButton());
        verify(apiClient(), never()).connect();
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
        Intent intent = new Intent();
        String expectedType = "expected type";
        intent.setType(expectedType);
        when(apiClient().getLeaderboardIntent(anyString())).thenReturn(intent);
        setSignedIn(true);
        setScreen(newScreen());
        clickForIntent(leaderboardButton(), hasType(expectedType));
    }

}
