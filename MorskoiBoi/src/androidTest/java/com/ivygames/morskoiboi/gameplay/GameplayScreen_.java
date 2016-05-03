package com.ivygames.morskoiboi.gameplay;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;
import com.ivygames.morskoiboi.screen.gameplay.TurnTimerController;

import org.hamcrest.Matcher;
import org.junit.Before;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.*;

public class GameplayScreen_ extends OnlineScreen_ {

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
        return ViewMatchers.withId(R.id.chat_button);
    }

}
