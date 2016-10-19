package com.ivygames.morskoiboi.lost;

import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.morskoiboi.screen.lost.LostScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class LostScreen_OpponentLeftDialogTest extends LostScreen_ {

    @Test
    public void WhenEnemyLeaves__OpponentLeftDialogDisplayed() {
        showScreen();
        ((LostScreen) screen()).onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);
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
