package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class LostScreenTest extends OnlineScreen_ {

    private Rules rules;

    @Before
    public void setup() {
        super.setup();
        rules = mock(Rules.class);
        RulesFactory.setRules(rules);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new LostScreen(activity());
    }

    @Test
    public void WhenScreenDisplayed__GamesCounterIncremented() {
        showScreen();
        verify(settings(), times(1)).incrementGamesPlayedCounter();
    }

    @Test
    public void WhenBackButtonPressedForAndroid__SelectGameScreenShown() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        FinishGame_BackToSelectGame();
    }

    @Test
    public void AfterNoPressedForAndroid__SelectGameScreenShown() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        clickOn(noButton());
        FinishGame_BackToSelectGame();
    }

    @Test
    public void AfterYesPressed__GameStateClearedBoardSetupScreenShown() {
        when(rules.getAllShipsSizes()).thenReturn(new int[]{});
        showScreen();
        clickOn(yesButton());
        verify(game, times(1)).clearState();
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

}
