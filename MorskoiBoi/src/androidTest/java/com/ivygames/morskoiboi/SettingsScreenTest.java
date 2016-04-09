package com.ivygames.morskoiboi;

import android.support.test.espresso.Espresso;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.ivygames.morskoiboi.screen.settings.SettingsScreen;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsScreenTest {

    @Rule
    public ScreenTestRule rule = new ScreenTestRule();

    private ScreenSetterResource idlingResource;
    private BattleshipActivity activity;
    private GoogleApiClientWrapper apiClient;

    //    @Mock
    private GameSettings settings = GameSettings.get();

    @Before
    public void setup() {
        activity = rule.getActivity();
        apiClient = rule.getApiClient();
    }

    @After
    public void teardown() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }

    @Test
    public void when_signed_in__sign_out_button_present() {
        when(apiClient.isConnected()).thenReturn(true);
        setScreen();
        assertThat(signInBar().getVisibility(), is(View.GONE));
        assertThat(signOutBar().getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_NOT_signed_in__sign_in_button_present() {
        when(apiClient.isConnected()).thenReturn(false);
        setScreen();
        assertThat(signInBar().getVisibility(), is(View.VISIBLE));
        assertThat(signOutBar().getVisibility(), is(View.GONE));
    }

    @Test
    public void when_sign_in_button_is_pressed__connect() {
        when(apiClient.isConnected()).thenReturn(false);
        setScreen();
        onView(withId(R.id.sign_in_button)).perform(click());
        verify(apiClient, times(1)).connect();
    }

    @Test
    public void when_sign_out_button_is_pressed__disconnect_and_hide_sign_out() {
        when(apiClient.isConnected()).thenReturn(true);
        setScreen();
        onView(withId(R.id.sign_out_btn)).perform(click());
        verify(apiClient, times(1)).disconnect();
        assertThat(signOutBar().getVisibility(), is(View.GONE));
    }

    @Test
    public void when_sound_button_is_pressed__sound_setting_toggled() {
        setScreen();
        settings.setSound(true);
        onView(withId(R.id.sound_btn)).perform(click());
        assertThat(settings.isSoundOn(), is(false));
    }

    @Test
    public void when_vibration_button_is_pressed__vibration_setting_toggled() {
        setScreen();
        settings.setVibration(true);
        Matcher<View> viewMatcher = withId(R.id.vibration_btn);
        onView(viewMatcher).perform(click());
        assertThat(settings.isVibrationOn(), is(false));
    }

    @Test
    public void when_report_problem_button_pressed__email_intent_fired() {
    }
//
//    @Test
//    public void when_rate_button_pressed__play_intent_fired() {
//    }

    private View signOutBar() {
        return getViewById(R.id.sign_out_bar);
    }

    private View signInBar() {
        return getViewById(R.id.sign_in_bar);
    }

    private View getViewById(int id) {
        return activity.findViewById(id);
    }

    private void setScreen() {
        idlingResource = new ScreenSetterResource(new Runnable() {
            @Override
            public void run() {
                activity.setScreen(new SettingsScreen(activity, apiClient, settings));
            }
        });
        Espresso.registerIdlingResources(idlingResource);
    }
}
