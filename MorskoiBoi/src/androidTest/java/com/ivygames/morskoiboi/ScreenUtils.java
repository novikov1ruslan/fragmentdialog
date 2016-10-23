package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.util.Log;
import android.view.View;

import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupLayout;
import com.ivygames.morskoiboi.screen.win.WinLayout;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;

public class ScreenUtils {
    public static final Matcher<View> WIN_LAYOUT = instanceOf(WinLayout.class);
    public static final Matcher<View> BOARD_SETUP_LAYOUT = instanceOf(BoardSetupLayout.class);

    public static void clickOn(Matcher<View> viewMatcher) {
        onView(viewMatcher).perform(click());
    }

    @NonNull
    public static Matcher<View> playButton() {
        return withId(R.id.play);
    }

    @NonNull
    public static Matcher<View> vsAndroid() {
        return withId(R.id.vs_android);
    }

    public static Matcher<View> autoSetup() {
        return withId(R.id.auto_setup);
    }

    public static Matcher<View> done() {
        return withId(R.id.done);
    }

    public static void checkDisplayed(Matcher<View> view) {
        onView(view).check(matches(isDisplayed()));
    }

    @NonNull
    public static Matcher<View> lostScreen() {
        return ViewMatchers.withText(R.string.lost);
    }

    /** Perform action of waiting for a specific view id. */
    public static ViewAction waitId(final Matcher<View> viewMatcher, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view matcher id during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    public static void waitFor(Matcher<View> viewMatcher, long millis) {
        onView(isRoot()).perform(waitId(viewMatcher, millis));
    }

    public static ViewAction clickXY(final int x, final int y){
        Log.i("TEST", "clicking on (" + x + "," + y + ")");

        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }
}
