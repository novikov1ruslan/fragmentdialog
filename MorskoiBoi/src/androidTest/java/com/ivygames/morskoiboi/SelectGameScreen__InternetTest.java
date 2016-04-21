package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SelectGameScreen__InternetTest extends SelectGameScreenTest {
    @Test
    public void WhenInternetButtonPressedButNotConnected__SignInDialogOpens() {
        setSignedIn(false);
        showScreen();
        clickOn(internetButton());
        checkDisplayed(signInDialog());
    }

    @Test
    public void WhenSignInButtonPressedForAchievementsDialog__ConnectedAndInternetGameScreenDisplayed() {
        WhenInternetButtonPressedButNotConnected__SignInDialogOpens();
        clickOn(signInButton());
        verify(apiClient(), times(1)).connect();
        signInSucceeded((SignInListener) screen());
        checkDisplayed(INTERNET_GAME_LAYOUT);
    }

    @Test
    public void WhenCancelPressedForAchievementsDialog__NotConnectedAndDialogDismissed() {
        WhenInternetButtonPressedButNotConnected__SignInDialogOpens();
        clickOn(cancelButton());
        verify(apiClient(), never()).connect();
        checkDoesNotExist(signInDialog());
    }

    @Test
    public void WhenBackPressedForAchievementsDialog__DialogDismissed() {
        WhenInternetButtonPressedButNotConnected__SignInDialogOpens();
        pressBack();
        checkDoesNotExist(signInDialog());
    }

    @Test
    public void WhenInternetPressedWhileConnected__InternetGameScreenOpens() {
        setSignedIn(true);
        showScreen();
        clickOn(internetButton());
        checkDisplayed(INTERNET_GAME_LAYOUT);
    }

    @NonNull
    protected Matcher<View> internetButton() {
        return withId(R.id.via_internet);
    }

    @NonNull
    protected Matcher<View> signInDialog() {
        return withText(R.string.internet_request);
    }
}
