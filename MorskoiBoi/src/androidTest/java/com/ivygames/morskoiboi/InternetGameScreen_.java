package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;
import com.ivygames.common.multiplayer.MultiplayerManager;

import org.hamcrest.Matcher;
import org.junit.Before;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;

public class InternetGameScreen_ extends ScreenTest {
    @Override
    public BattleshipScreen newScreen() {
        return new InternetGameScreen(activity);
    }

    @NonNull
    protected final Matcher<View> invite() {
        return withId(R.id.invite_player_button);
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
