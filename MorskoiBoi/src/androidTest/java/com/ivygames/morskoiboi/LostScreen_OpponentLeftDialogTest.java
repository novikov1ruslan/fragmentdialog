package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.GameEvent;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;

public class LostScreen_OpponentLeftDialogTest extends LostScreen_ {

    @Test
    public void WhenEnemyLeaves__OpponentLeftDialogDisplayed() {
        showScreen();
        ((LostScreen) screen()).onEventMainThread(GameEvent.OPPONENT_LEFT);
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
