package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;
import com.ivygames.morskoiboi.screen.gameplay.TurnTimerController;

import org.junit.Before;
import org.mockito.Mockito;

public class GameplayScreen_ extends OnlineScreenTest {

    protected TurnTimerController timeController;

    @Before
    public void setup() {
        super.setup();
        timeController = Mockito.mock(TurnTimerController.class);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new GameplayScreen(activity(), timeController);
    }

}
