package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsScreenTest {

    @Rule
    public ActivityTestRule<BattleshipActivity> mActivityRule = new ActivityTestRule<>(
            BattleshipActivity.class);

    @Before
    public void setup() {
        onView(withId(R.id.settings_button)).perform(click());
    }

    @Test
    public void when_not_signed_in__sign_in_button_present() {
        int signInVisibility = mActivityRule.getActivity().findViewById(R.id.sign_in_bar).getVisibility();
        int signOutVisibility = mActivityRule.getActivity().findViewById(R.id.sign_in_bar).getVisibility();
        assertThat(signInVisibility, is(View.VISIBLE));
        assertThat(signOutVisibility, is(View.GONE));
    }

    @Test
    public void when_sign_in_button_is_pressed__sign_in_intent_fired() {
//        onView(withId(R.id.settings_button)).perform(click());
    }

    @Test
    public void when_sound_button_is_pressed__sound_setting_toggled() {
    }

    @Test
    public void when_vibration_button_is_pressed__vibration_setting_toggled() {
    }

    @Test
    public void when_report_problem_button_pressed__email_intent_fired() {
    }

    @Test
    public void when_rate_button_pressed__play_intent_fired() {
    }

}
