package com.ivygames.morskoiboi.win;

import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class WinScreen_OpponentLeftDialogTest extends WinScreen_ {

    @Test
    public void WhenEnemyLeaves__OpponentLeftDialogDisplayed() {
        showScreen();
        ((WinScreen) screen()).onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);
        checkDisplayed(opponentLeftDialog());
    }

    @Test
    public void PressingBack__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenEnemyLeaves__OpponentLeftDialogDisplayed();
        pressBack();
        checkDoesNotExist(opponentLeftDialog());
        FinishGame_BackToSelectGame();
    }

    @Test
    public void PressingOk__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenEnemyLeaves__OpponentLeftDialogDisplayed();
        clickOn(okButton());
        checkDoesNotExist(opponentLeftDialog());
        FinishGame_BackToSelectGame();
    }

}
