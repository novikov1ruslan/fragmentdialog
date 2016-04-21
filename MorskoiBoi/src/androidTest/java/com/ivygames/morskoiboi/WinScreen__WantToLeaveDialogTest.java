package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Game;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class WinScreen__WantToLeaveDialogTest extends WinScreenTest {
    @Test
    public void WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed() {
        surrendered = false;
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__RemovesDialogScreenRemains() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(cancelButton());
        checkDisplayed(WIN_LAYOUT);
        checkDoesNotExist(wantToLeaveDialog());
    }

    @Test
    public void PressingBackOnWantToLeaveDialog__RemovesDialogScreenRemains() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        pressBack();
        checkDisplayed(WIN_LAYOUT);
        checkDoesNotExist(wantToLeaveDialog());
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__SelectGameScreenDisplayed() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(okButton());
        backToSelectGameCommand();
    }

    @NonNull
    protected Matcher<View> wantToLeaveDialog() {
        return withText(getString(R.string.want_to_leave_room, OPPONENT_NAME));
    }

    @Test
    public void AfterNoPressedForNonAndroid__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        WhenOpponentNotSurrendered__YesNoButtonsShowed();
        clickOn(noButton());
        String message = getString(R.string.want_to_leave_room, OPPONENT_NAME);
        checkDisplayed(withText(message));
    }
}
