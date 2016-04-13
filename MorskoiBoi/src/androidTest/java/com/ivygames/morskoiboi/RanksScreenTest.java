package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;


public class RanksScreenTest extends ScreenTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new BoardSetupScreen(activity());
    }

    @Test
    public void WhenBackButtonPressed_SelectGameScreenOpens() {
        setScreen(newScreen());
        pressBack();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

}
