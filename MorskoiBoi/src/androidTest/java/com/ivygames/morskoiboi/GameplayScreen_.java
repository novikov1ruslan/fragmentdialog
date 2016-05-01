package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;
import com.ivygames.morskoiboi.screen.gameplay.TurnTimerController;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.mockito.Mockito;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.*;

public class GameplayScreen_ extends OnlineScreenTest {

    protected TurnTimerController timeController;

    @Before
    public void setup() {
        super.setup();
        timeController = mock(TurnTimerController.class);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new GameplayScreen(activity(), timeController);
    }

    @NonNull
    protected final Matcher<View> chat() {
        return withId(R.id.chat_button);
    }

}
