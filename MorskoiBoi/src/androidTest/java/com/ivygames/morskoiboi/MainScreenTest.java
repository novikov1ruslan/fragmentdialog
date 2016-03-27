package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainScreenTest {

    @Rule
    public ActivityTestRule<BattleshipActivity> mActivityRule = new ActivityTestRule<>(
            BattleshipActivity.class);

    @Test
    public void changeText_sameActivity() {
//        onView(withText("Error")).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.play)).perform(click());
        onView(Matchers.<View>instanceOf(SelectGameLayout.class)).check(matches(isDisplayed()));
//        onView(Matchers.<View>instanceOf(BoardSetupLayout.class)).check(matches(isDisplayed()));
    }
}
