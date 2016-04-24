package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.junit.Before;

public class GameplayScreen_ extends OnlineScreenTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new GameplayScreen(activity());
    }

}
