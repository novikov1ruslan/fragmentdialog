package com.ivygames.morskoiboi.gameplay;

import com.ivygames.morskoiboi.model.Game;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class GameplayScreen_WantToLeaveDialogTest extends GameplayScreen_ {

    @Test
    public void WhenBackPressedForInternetGame__DialogDisplayed() {
        setGameType(Game.Type.INTERNET);
        showScreen();
        pressBack();
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void WhenBackPressedForBluetoothGame__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        checkDisplayed(wantToLeaveDialog());
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__RemovesDialogScreenDoesNotChange() {
        WhenBackPressedForBluetoothGame__WantToLeaveDialogDisplayed();
        clickOn(cancelButton());
        checkDoesNotExist(wantToLeaveDialog());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void PressingBackOnWantToLeaveDialog__RemovesDialogScreenDoesNotChange() {
        WhenBackPressedForBluetoothGame__WantToLeaveDialogDisplayed();
        pressBack();
        checkDoesNotExist(wantToLeaveDialog());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__GameFinishes_SelectGameScreenDisplayed() {
        WhenBackPressedForBluetoothGame__WantToLeaveDialogDisplayed();
        clickOn(okButton());
        FinishGame_BackToSelectGame();
    }
}
