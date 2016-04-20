package com.ivygames.morskoiboi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainScreen_AchievementsTest extends MainScreenTest {
    @NonNull
    protected static Matcher<View> achievementsButton() {
        return withId(R.id.achievements_button);
    }

    @NonNull
    protected static Matcher<View> achievementsDialog() {
        return withText(R.string.achievements_request);
    }

    @Test
    public void WhenAchievementsButtonPressedWhenNonConnected__SignInDialogDisplayed() {
        setSignedIn(false);
        setScreen(newScreen());
        clickOn(achievementsButton());
        checkDisplayed(achievementsDialog());
    }

    @Test
    public void WhenSignInButtonPressedForAchievementsDialog__Connected() {
        WhenAchievementsButtonPressedWhenNonConnected__SignInDialogDisplayed();
        clickOn(signInButton());
        verify(apiClient(), times(1)).connect();
    }

    @Test
    public void WhenCancelPressedForAchievementsDialog__NotConnectedAndDialogDismissed() {
        WhenAchievementsButtonPressedWhenNonConnected__SignInDialogDisplayed();
        clickOn(cancelButton());
        verify(apiClient(), never()).connect();
        checkDoesNotExist(achievementsDialog());
    }

    @Test
    public void WhenBackPressedForAchievementsDialog__DialogDismissed() {
        WhenAchievementsButtonPressedWhenNonConnected__SignInDialogDisplayed();
        pressBack();
        checkDoesNotExist(achievementsDialog());
    }

    @Test
    public void WhenAchievementsButtonPressedWhenConnected__achievements_intent_is_fired() {
        String expectedType = "expected type";
        when(apiClient().getAchievementsIntent()).thenReturn(new Intent().setType(expectedType));
        setSignedIn(true);
        showScreen();
        clickForIntent(achievementsButton(), hasType(expectedType));
    }

}
