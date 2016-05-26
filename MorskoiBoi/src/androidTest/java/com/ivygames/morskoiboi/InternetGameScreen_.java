package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;

import org.hamcrest.Matcher;
import org.junit.Before;

import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class InternetGameScreen_ extends ScreenTest {
    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new InternetGameScreen(activity);
    }

    @NonNull
    protected Matcher<View> inviteButton() {
//        return withId(R.id.invite_player_button);
        return withText(R.string.invite_player);
    }

    @NonNull
    protected Matcher<View> waitDialog() {
        return withText(R.string.please_wait);
    }
}
