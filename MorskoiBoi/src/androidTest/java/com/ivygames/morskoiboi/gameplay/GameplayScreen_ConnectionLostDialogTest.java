package com.ivygames.morskoiboi.gameplay;

import com.ivygames.morskoiboi.model.GameEvent;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static org.mockito.Mockito.*;

public class GameplayScreen_ConnectionLostDialogTest extends GameplayScreen_ {

    @Test
    public void WhenConnectionLostAndGameHasFinished__DialogNotDisplayed() {
        showScreen();
        when(game.hasFinished()).thenReturn(true);
        ((GameplayScreen) screen()).onEventMainThread(GameEvent.CONNECTION_LOST);
        checkDoesNotExist(connectionLostDialog());
    }

    @Test
    public void WhenConnectionLostAndGameHasNotFinished__DialogDisplayed() {
        // TODO: check this strange condition
        showScreen();
        when(game.hasFinished()).thenReturn(false);
        ((GameplayScreen) screen()).onEventMainThread(GameEvent.CONNECTION_LOST);
        checkDisplayed(connectionLostDialog());
    }

    @Test
    public void PressingBack__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenConnectionLostAndGameHasNotFinished__DialogDisplayed();
        pressBack();
        checkDoesNotExist(connectionLostDialog());
        FinishGame_BackToSelectGame();
    }

    @Test
    public void PressingOk__DismissesDialog_GameFinishes_SelectGameScreensShown() {
        WhenConnectionLostAndGameHasNotFinished__DialogDisplayed();
        clickOn(okButton());
        checkDoesNotExist(connectionLostDialog());
        FinishGame_BackToSelectGame();
    }

}
