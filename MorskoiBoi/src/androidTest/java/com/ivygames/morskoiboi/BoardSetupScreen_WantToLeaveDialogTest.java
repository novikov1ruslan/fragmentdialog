package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Game;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;

public class BoardSetupScreen_WantToLeaveDialogTest extends BoardSetupScreenTest {
    @Test
    public void WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__DialogDismissed() {
        WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(cancelButton());
        checkDoesNotExist(wantToLeaveDialog());
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__SelectGameScreenDisplayed() {
        WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(okButton());
        FinishGame_BackToSelectGame();
    }
}
