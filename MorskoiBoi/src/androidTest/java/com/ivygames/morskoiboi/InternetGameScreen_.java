package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;

import org.junit.Before;

public class InternetGameScreen_ extends ScreenTest {
    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new InternetGameScreen(activity());
    }

}
