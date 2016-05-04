package com.ivygames.morskoiboi.gameplay;

import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Game;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

public class GameplayScreen_SurrenderDialog extends GameplayScreen_ {

    @Test
    public void WhenBackPressedForInternetGame_AndEnemyIsReady__DialogDisplayed() {
        setGameType(Game.Type.INTERNET);
        showScreen();
        when(player.isOpponentReady()).thenReturn(true);
        pressBack();
        checkDisplayed(surrenderDialog());
    }

    @Test
    public void WhenBackPressedForBluetoothGame_AndEnemyIsReady__DialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        when(player.isOpponentReady()).thenReturn(true);
        pressBack();
        checkDisplayed(surrenderDialog());
    }

    @Test
    public void WhenBackPressed__DialogDismissed_ScreenRemains() {
        WhenBackPressedForInternetGame_AndEnemyIsReady__DialogDisplayed();
        pressBack();
        checkDoesNotExist(surrenderDialog());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void WhenNoPressed__DialogDismissed_ScreenRemains() {
        WhenBackPressedForInternetGame_AndEnemyIsReady__DialogDisplayed();
        clickOn(cancelButton());
        checkDoesNotExist(surrenderDialog());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void WhenYesPressed__DialogDismissed_GameFinished_SelectGameDisplayed() {
        WhenBackPressedForInternetGame_AndEnemyIsReady__DialogDisplayed();
        clickOn(okButton());
        checkDoesNotExist(surrenderDialog());
        FinishGame_BackToSelectGame();
    }

    private Matcher<View> surrenderDialog() {
        int penalty = 3000;
        String message = getString(R.string.surrender_question, "" + -penalty);
        return withText(message);
    }
}
