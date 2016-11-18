package com.ivygames.morskoiboi.gameplay;

import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.RulesUtils;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameplayScreen_SurrenderDialog extends GameplayScreen_ {
    private static final int PENALTY = 3000;

    @Before
    public void setup() {
        super.setup();
//        when(rules.getAllShipsSizes()).thenReturn(FLEET);
    }

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
        Collection<Ship> ships = new ArrayList<>(Arrays.asList(new Ship(1), new Ship(1)));
//        verify(settings(), times(1)).setProgressPenalty(RulesUtils.calcSurrenderPenalty(ships, FLEET));
        backToSelectGame();
    }

    private Matcher<View> surrenderDialog() {
        String message = getString(R.string.surrender_question, "-" + PENALTY);
        return withText(message);
    }
}
