package com.ivygames.morskoiboi.selectgame;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.common.ui.SignInListener;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ScreenTest;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class SelectGameScreen_InternetTest extends SelectGameScreen_ {
    @Test
    public void WhenInternetButtonPressedButNotConnected__SignInDialogOpens() {
        setSignedIn(false);
        showScreen();
        clickOn(internet());
        checkDisplayed(signInDialog());
    }

    @Test
    public void WhenSignInButtonPressedForAchievementsDialog__ConnectedAndInternetGameScreenDisplayed() {
        WhenInternetButtonPressedButNotConnected__SignInDialogOpens();
        clickOn(signInButton());
        verifyConnected();
        signInSucceeded((SignInListener) screen());
        checkDisplayed(ScreenTest.INTERNET_GAME_LAYOUT);
    }

    @Test
    public void WhenCancelPressedForAchievementsDialog__NotConnectedAndDialogDismissed() {
        WhenInternetButtonPressedButNotConnected__SignInDialogOpens();
        clickOn(cancelButton());
        verifyDisconnected();
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
        clickOn(internet());
        checkDisplayed(ScreenTest.INTERNET_GAME_LAYOUT);
    }

    @NonNull
    protected Matcher<View> signInDialog() {
        return ViewMatchers.withText(R.string.internet_request);
    }
}
