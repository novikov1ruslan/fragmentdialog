package com.ivygames.morskoiboi.boardsetup;

import com.ivygames.morskoiboi.OnlineScreen_;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.BOARD_SETUP_LAYOUT;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class BoardSetupScreen_WantToLeaveDialogTest extends BoardSetupScreen_ {
    @Test
    public void WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed() {
        setGameType(OnlineScreen_.Type.BLUETOOTH);
        showScreen();

        pressBack();

        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void PressingBackOnWantToLeaveDialog__DialogDismissed() {
        WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed();

        pressBack();

        checkDoesNotExist(wantToLeaveDialog());
        checkDisplayed(BOARD_SETUP_LAYOUT);
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

        backToSelectGame();
    }

}
