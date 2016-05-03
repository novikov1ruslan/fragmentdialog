package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.hamcrest.Matcher;
import org.junit.Before;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class BoardSetupScreen_ extends OnlineScreen_ {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new BoardSetupScreen(activity());
    }

    @NonNull
    protected final Matcher<View> placeInstructions() {
        return withText(R.string.place_instruction);
    }

    @NonNull
    protected final Matcher<View> rotateInstructions() {
        return withText(R.string.rotate_instruction);
    }

    @NonNull
    protected final Matcher<View> mustSetShipsMessage() {
        return withText(R.string.ships_setup_validation);
    }

    protected final Matcher<View> autoSetup() {
        return withId(R.id.auto_setup);
    }

    protected final Matcher<View> done() {
        return withId(R.id.done);
    }

    protected final Matcher<View> boardView() {
        return withId(R.id.board_view);
    }

    protected final Matcher<View> help() {
        return withId(R.id.help_button);
    }

    protected final Matcher<View> gotIt() {
        return withId(R.id.got_it_button);
    }
}
