package com.ivygames.morskoiboi.selectgame;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.common.SignInListener;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ScreenTest;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SelectGameScreen_InternetTest extends SelectGameScreen_ {
    @Test
    public void WhenInternetButtonPressedButNotConnected__SignInDialogOpens() {
        setSignedIn(false);
        showScreen();
        ScreenTest.clickOn(internet());
        checkDisplayed(signInDialog());
    }

    @Test
    public void WhenSignInButtonPressedForAchievementsDialog__ConnectedAndInternetGameScreenDisplayed() {
        WhenInternetButtonPressedButNotConnected__SignInDialogOpens();
        ScreenTest.clickOn(signInButton());
        verify(apiClient(), times(1)).connect();
        signInSucceeded((SignInListener) screen());
        checkDisplayed(ScreenTest.INTERNET_GAME_LAYOUT);
    }

    @Test
    public void WhenCancelPressedForAchievementsDialog__NotConnectedAndDialogDismissed() {
        WhenInternetButtonPressedButNotConnected__SignInDialogOpens();
        ScreenTest.clickOn(cancelButton());
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
        ScreenTest.clickOn(internet());
        checkDisplayed(ScreenTest.INTERNET_GAME_LAYOUT);
    }

    @NonNull
    protected Matcher<View> signInDialog() {
        return ViewMatchers.withText(R.string.internet_request);
    }
}
