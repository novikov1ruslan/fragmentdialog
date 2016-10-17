package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.screen.win.WinLayout;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;

public class ScreenUtils {
    public static final Matcher<View> WIN_LAYOUT = instanceOf(WinLayout.class);

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
}
