package com.ivygames.morskoiboi.gameplay;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.hamcrest.Matcher;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class GameplayScreen_ extends OnlineScreen_ {

    TurnTimerController timeController;

    @Before
    public void setup() {
        super.setup();
        timeController = mock(TurnTimerController.class);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new GameplayScreen(activity, game, session, timeController);
    }

    @NonNull
    final Matcher<View> chat() {
        return ViewMatchers.withId(R.id.chat_button);
    }

}
