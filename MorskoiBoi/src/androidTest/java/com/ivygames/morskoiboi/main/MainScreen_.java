package com.ivygames.morskoiboi.main;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ScreenTest;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class MainScreen_ extends ScreenTest {

    @Override
    public BattleshipScreen newScreen() {
        return new MainScreen(activity, apiClient(), settings());
    }

    @NonNull
    protected Matcher<View> pusOneButton() {
        return withId(R.id.plus_one_button);
    }
}
