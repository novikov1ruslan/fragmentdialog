package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Game;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;

public class GameplayScreen_WantToLeaveDialogTest extends GameplayScreen_ {

    @Test
    public void WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__RemovesDialogScreenDoesNotChange() {
        WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(cancelButton());
        checkDoesNotExist(wantToLeaveDialog());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__GameFinishes_SelectGameScreenDisplayed() {
        WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(okButton());
        backToSelectGameCommand();
    }
}
