package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Game;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class LostScreen_WantToLeaveDialogTest extends LostScreenTest {

    @Test
    public void AfterNoPressedForNonAndroid__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        clickOn(noButton());
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void WhenBackButtonPressedForNonAndroid__WantToLeaveDialogShown() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__SelectGameScreenDisplayed() {
        WhenBackButtonPressedForNonAndroid__WantToLeaveDialogShown();
        clickOn(okButton());
        backToSelectGameCommand();
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__RemovesDialog() {
        WhenBackButtonPressedForNonAndroid__WantToLeaveDialogShown();
        clickOn(cancelButton());
        checkDisplayed(lostScreen());
        checkDoesNotExist(wantToLeaveDialog());
    }

    @Test
    public void PressingBackOnWantToLeaveDialog__RemovesDialog() {
        WhenBackButtonPressedForNonAndroid__WantToLeaveDialogShown();
        pressBack();
        checkDisplayed(lostScreen());
        checkDoesNotExist(wantToLeaveDialog());
    }

    @NonNull
    protected Matcher<View> lostScreen() {
        return withText(R.string.lost);
    }

    @NonNull
    protected Matcher<View> wantToLeaveDialog() {
        return withText(getString(R.string.want_to_leave_room, OPPONENT_NAME));
    }
}
