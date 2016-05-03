package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Game;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;

public class WinScreen_WantToLeaveDialogTest extends WinScreen_ {

    @Test
    public void WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed() {
        surrendered = false;
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void AfterNoPressedForNonAndroid__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        surrendered = false;
        showScreen();
        clickOn(noButton());
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__RemovesDialogScreenRemains() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(cancelButton());
        checkDoesNotExist(wantToLeaveDialog());
        checkDisplayed(WIN_LAYOUT);
    }

    @Test
    public void PressingBackOnWantToLeaveDialog__RemovesDialogScreenRemains() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        pressBack();
        checkDoesNotExist(wantToLeaveDialog());
        checkDisplayed(WIN_LAYOUT);
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__SelectGameScreenDisplayed() {
        WhenBackPressedForNotSurrenderedNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(okButton());
        FinishGame_BackToSelectGame();
    }

}
