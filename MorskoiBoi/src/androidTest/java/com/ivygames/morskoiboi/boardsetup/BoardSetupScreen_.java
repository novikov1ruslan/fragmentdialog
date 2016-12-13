package com.ivygames.morskoiboi.boardsetup;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.RandomOrientationBuilder;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.hamcrest.Matcher;
import org.junit.Before;

import java.util.Random;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class BoardSetupScreen_ extends OnlineScreen_ {

    protected MyOrientationBuilder mOrientationBuilder = new MyOrientationBuilder();

    @Before
    public void setup() {
        super.setup();

    }

    @Override
    public BattleshipScreen newScreen() {
        return new BoardSetupScreen(activity, game, session);
    }

    @NonNull
    protected final Matcher<View> placeInstructions() {
        return ViewMatchers.withText(R.string.place_instruction);
    }

    @NonNull
    protected final Matcher<View> rotateInstructions() {
        return withText(R.string.rotate_instruction);
    }

    @NonNull
    protected final Matcher<View> mustSetShipsMessage() {
        return withText(R.string.ships_setup_validation);
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

    public static class MyOrientationBuilder extends RandomOrientationBuilder {
        private Ship.Orientation orientation;

        public MyOrientationBuilder() {
            super(new Random());
        }

        @Override
        public Ship.Orientation nextOrientation() {
            if (orientation == null) {
                return super.nextOrientation();
            }

            return orientation;
        }

        public void setOrientation(Ship.Orientation orientation) {
            this.orientation = orientation;
        }
    }
}
