package com.ivygames.morskoiboi.boardsetup;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


public class BoardSetupScreenTest extends BoardSetupScreen_ {

    public void WhenAutoPressed__BoardIsSet() {
        showScreen();
        clickOn(autoSetup());
        // TODO:
    }

    @Test
    public void WhenBoardIsSet_PressingDone__OpensGameplayScreen() {
        showScreen();
        when(rules.isBoardSet(any(Board.class))).thenReturn(true);
        clickOn(done());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void WhenBoardNotSet__PressingDone_DisplaysMustSetShipsMessage() {
        showScreen();
        clickOn(done());
        checkDisplayed(mustSetShipsMessage());
    }

    public void ForInternetGame_IfPlayerSetsShipsForMoreThan60Seconds__SessionTimeoutDialogShown() {
        // TODO:
    }

    public void ForNonInternetGames_SetupTimeIsUnlimited() {
    // TODO:
    }

    public void WhenScreenDestroyed__AllCroutonsCanceled() {
    // TODO:
    }

    @Test
    public void WhenPressedBackForAndroidGame__GameFinishes_OpensSelectGameScreen() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        FinishGame_BackToSelectGame();
    }

}