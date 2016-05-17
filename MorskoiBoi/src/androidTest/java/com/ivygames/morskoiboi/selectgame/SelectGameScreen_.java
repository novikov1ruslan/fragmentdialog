package com.ivygames.morskoiboi.selectgame;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ScreenTest;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class SelectGameScreen_ extends ScreenTest {

    @Override
    public BattleshipScreen newScreen() {
        return new SelectGameScreen(activity, settings());
    }

    @NonNull
    protected final Matcher<View> viaBtButton() {
        return withId(R.id.via_bluetooth);
    }

    @NonNull
    protected final Matcher<View> internet() {
        return withId(R.id.via_internet);
    }
}
