package com.ivygames.morskoiboi.boardsetup;

import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class BoardSetupScreen_OpponentLeftDialogTest extends BoardSetupScreen_ {

    @Test
    public void WhenEnemyLeaves__OpponentLeftDialogDisplayed() {
        showScreen();
        ((BoardSetupScreen) screen()).onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);
        checkDisplayed(opponentLeftDialog());
    }

    @Test
    public void PressingBack__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenEnemyLeaves__OpponentLeftDialogDisplayed();
        pressBack();
        checkDoesNotExist(opponentLeftDialog());
        backToSelectGame();
    }

    @Test
    public void PressingOk__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenEnemyLeaves__OpponentLeftDialogDisplayed();
        clickOn(okButton());
        checkDoesNotExist(opponentLeftDialog());
        backToSelectGame();
    }

}
