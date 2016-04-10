package com.ivygames.morskoiboi;

import android.content.Intent;
import android.view.View;

import com.ivygames.common.PlayUtils;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreenLayout;
import com.ivygames.morskoiboi.screen.settings.SettingsScreen;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class SettingsScreenTest extends ScreenTest {


    private GameSettings settings = GameSettings.get();

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new SettingsScreen(activity(), apiClient(), settings);
    }

    @Test
    public void when_signed_in__sign_out_button_present() {
        setSignedIn(true);
        assertThat(signInBar().getVisibility(), is(View.GONE));
        assertThat(signOutBar().getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_NOT_signed_in__sign_in_button_present() {
        setSignedIn(false);
        assertThat(signInBar().getVisibility(), is(View.VISIBLE));
        assertThat(signOutBar().getVisibility(), is(View.GONE));
    }

    @Test
    public void when_sign_in_button_is_pressed__connect() {
        setSignedIn(false);
        onView(withId(R.id.sign_in_button)).perform(click());
        verify(apiClient(), times(1)).connect();
    }

    @Test
    public void when_sign_out_button_is_pressed__disconnect_and_hide_sign_out() {
        setSignedIn(true);
        onView(withId(R.id.sign_out_btn)).perform(click());
        verify(apiClient(), times(1)).disconnect();
        assertThat(signOutBar().getVisibility(), is(View.GONE));
    }

    @Test
    public void when_sound_button_is_pressed__sound_setting_toggled() {
        setScreen(newScreen());
        settings.setSound(true);
        onView(withId(R.id.sound_btn)).perform(click());
        assertThat(settings.isSoundOn(), is(false));
    }

//    @Test
//    public void when_vibration_button_is_pressed__vibration_setting_toggled() {
//        setScreen(screen());
//        settings.setVibration(true);
//        Matcher<View> viewMatcher = withId(R.id.vibration_btn);
//        onView(viewMatcher).perform(click());
//        assertThat(settings.isVibrationOn(), is(false));
    //  TODO:
//    }

    @Test
    public void when_report_problem_button_pressed__email_intent_fired() {
        setScreen(newScreen());
        Matcher<Intent> expectedIntent = hasAction(Intent.ACTION_SENDTO);
        clickForIntent(withId(R.id.report_problem), expectedIntent);
    }

    @Test
    public void when_rate_button_pressed__play_intent_fired() {
        setScreen(newScreen());
        Intent intent = PlayUtils.rateIntent(activity().getPackageName());
        Matcher<Intent> expectedIntent = allOf(hasAction(intent.getAction()), hasData(intent.getData()));
        clickForIntent(withId(R.id.rate_btn), expectedIntent);
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
        setScreen(newScreen());
        pressBack();
        onView(Matchers.<View>instanceOf(MainScreenLayout.class)).check(matches(isDisplayed()));
    }

    private View signOutBar() {
        return viewById(R.id.sign_out_bar);
    }

    private View signInBar() {
        return viewById(R.id.sign_in_bar);
    }

}
