package com.ivygames.morskoiboi.gameplay;

import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static org.mockito.Mockito.when;

public class GameplayScreen_OpponentLeftDialogTest extends GameplayScreen_ {

    @Test
    public void WhenOpponentLeftAndOpponentNotReady__OpponentLeftDialogDisplayed() {
        showScreen();
        when(player.isOpponentReady()).thenReturn(false);
        ((GameplayScreen)screen()).onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);
        checkDisplayed(opponentLeftDialog());
    }

    @Test
    public void PressingBack__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenOpponentLeftAndOpponentNotReady__OpponentLeftDialogDisplayed();
        pressBack();
        checkDoesNotExist(opponentLeftDialog());
        backToSelectGame();
    }

    @Test
    public void PressingOk__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenOpponentLeftAndOpponentNotReady__OpponentLeftDialogDisplayed();
        clickOn(okButton());
        checkDoesNotExist(opponentLeftDialog());
        backToSelectGame();
    }
}
