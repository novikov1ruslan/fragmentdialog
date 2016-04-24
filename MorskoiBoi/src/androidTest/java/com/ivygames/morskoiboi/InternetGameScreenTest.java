package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;

public class InternetGameScreenTest extends ScreenTest {
    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new InternetGameScreen(activity());
    }

    @Test
    public void WhenBackPressed__SelectGameScreenOpens() {
        showScreen();
        pressBack();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }
}
