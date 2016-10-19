package com.ivygames.morskoiboi.gameplay;

import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class GameplayScreen_ConnectionLostDialogTest extends GameplayScreen_ {

    @Test
    public void WhenConnectionLost__DialogDisplayed() {
        // TODO: check this strange condition
        showScreen();
        ((GameplayScreen) screen()).onConnectionLost(MultiplayerEvent.CONNECTION_LOST);
        checkDisplayed(connectionLostDialog());
    }

    @Test
    public void PressingBack__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenConnectionLost__DialogDisplayed();
        pressBack();
        checkDoesNotExist(connectionLostDialog());
        backToSelectGame();
    }

    @Test
    public void PressingOk__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenConnectionLost__DialogDisplayed();
        clickOn(okButton());
        checkDoesNotExist(connectionLostDialog());
        backToSelectGame();
    }

}
