package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.help.HelpScreen;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;


public class HelpScreenTest extends ScreenTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new HelpScreen(activity);
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
        setScreen(newScreen());
        pressBack();
        checkDisplayed(MAIN_LAYOUT);
    }

}
