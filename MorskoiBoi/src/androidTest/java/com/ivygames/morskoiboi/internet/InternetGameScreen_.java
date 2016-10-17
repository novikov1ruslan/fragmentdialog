package com.ivygames.morskoiboi.internet;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ScreenTest;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class InternetGameScreen_ extends ScreenTest {
    @Override
    public BattleshipScreen newScreen() {
        return new InternetGameScreen(activity);
    }

    @NonNull
    protected final Matcher<View> invite() {
        return ViewMatchers.withId(R.id.invite_player_button);
    }

    @NonNull
    protected final Matcher<View> viewInvitations() {
        return withId(R.id.view_invitations_button);
    }

    @NonNull
    protected final Matcher<View> quickGame() {
        return withId(R.id.quick_game_button);
    }

    @NonNull
    protected final Matcher<View> waitDialog() {
        return withText(R.string.please_wait);
    }

}
