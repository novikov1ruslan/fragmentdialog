package com.ivygames.morskoiboi.boardsetup;

import com.ivygames.morskoiboi.model.GameEvent;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;

public class BoardSetupScreen_OpponentLeftDialogTest extends BoardSetupScreen_ {

    @Test
    public void WhenEnemyLeaves__OpponentLeftDialogDisplayed() {
        showScreen();
        ((BoardSetupScreen) screen()).onEventMainThread(GameEvent.OPPONENT_LEFT);
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
