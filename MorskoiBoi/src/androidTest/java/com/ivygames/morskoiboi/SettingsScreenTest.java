package com.ivygames.morskoiboi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.common.PlayUtils;
import com.ivygames.common.VibratorWrapper;
import com.ivygames.common.ui.SignInListener;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.settings.SettingsScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SettingsScreenTest extends ScreenTest {

    private VibratorWrapper vibratorFacade;

    @Before
    public void setup() {
        super.setup();
        vibratorFacade = mock(VibratorWrapper.class);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new SettingsScreen(activity, vibratorFacade);
    }

    @Test
    public void when_signed_in__sign_out_button_present() {
        setSignedIn(true);
        setScreen(newScreen());
        assertThat(signInBar().getVisibility(), is(View.GONE));
        assertThat(signOutBar().getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_NOT_signed_in__sign_in_button_present() {
        setSignedIn(false);
        setScreen(newScreen());
        assertThat(signInBar().getVisibility(), is(View.VISIBLE));
        assertThat(signOutBar().getVisibility(), is(View.GONE));
    }

    @Test
    public void when_sign_in_button_is_pressed__connect() {
        setSignedIn(false);
        setScreen(newScreen());
        onView(withId(R.id.sign_in_button)).perform(click());
        verifyConnected();
        signInSucceeded((SignInListener) screen());
        assertThat(signOutBar().getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_sign_out_button_is_pressed__disconnect_and_hide_sign_out() {
        setSignedIn(true);
        setScreen(newScreen());

        onView(withId(R.id.sign_out_btn)).perform(click());

        verifyDisconnected();
        assertThat(signOutBar().getVisibility(), is(View.GONE));
    }

    @Test
    public void if_sound_on__on_image_is_shown() {
        setSound(true);
        setScreen(newScreen());
        onView(soundButton()).check(matches(withDrawable(R.drawable.sound_on)));
    }

    @Test
    public void if_sound_off__off_image_is_shown() {
        setSound(false);
        setScreen(newScreen());
        onView(soundButton()).check(matches(withDrawable(R.drawable.sound_off)));
    }

    @Test
    public void when_sound_button_is_pressed__sound_setting_toggled() {
        setSound(true);
        setScreen(newScreen());
        onView(withId(R.id.sound_btn)).perform(click());
        verify(settings(), times(1)).setSound(false);
    }

    @Test
    public void when_vibration_available__vibration_button_present() {
        hasVibration();
        setScreen(newScreen());
        assertThat(vibrationContainer().getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_vibration_NOT_available__vibration_button_NOT_present() {
        vibrationAbsent();
        setScreen(newScreen());
        assertThat(vibrationContainer().getVisibility(), is(not(View.VISIBLE)));
    }

    @Test
    public void if_vibration_on__on_image_is_shown() {
        hasVibration();
        setVibration(true);
        setScreen(newScreen());
        onView(vibrationButton()).check(matches(withDrawable(R.drawable.vibrate_on)));
    }

    @Test
    public void if_vibration_off__off_image_is_shown() {
        hasVibration();
        setVibration(false);
        setScreen(newScreen());
        onView(vibrationButton()).check(matches(withDrawable(R.drawable.vibrate_off)));
    }

    @Test
    public void when_vibration_button_is_pressed__vibration_setting_toggled() {
        hasVibration();
        setVibration(true);
        setScreen(newScreen());
        Matcher<View> viewMatcher = vibrationButton();
        onView(viewMatcher).perform(click());
        verify(settings(), times(1)).setVibration(false);
    }

    @Test
    public void when_device_can_resolve_report_a_problem_intent__RP_button_present() {
        setCanResolveIntent(true);

        setScreen(newScreen());

        assertThat(reportProblem().getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_device_CANNOT_resolve_report_a_problem_intent__RP_button_NOT_present() {
        setCanResolveIntent(false);
        setScreen(newScreen());
        assertThat(reportProblem().getVisibility(), is(not(View.VISIBLE)));
    }

    @Test
    public void when_report_problem_button_pressed__email_intent_fired() {
        setCanResolveIntent(true);

        setScreen(newScreen());

        Matcher<Intent> expectedIntent = anyOf(hasAction(Intent.ACTION_SENDTO), hasAction(Intent.ACTION_CHOOSER));
        clickForIntent(withId(R.id.report_problem), expectedIntent);
    }

    @Test
    public void when_rate_button_pressed__play_intent_fired() {
        setScreen(newScreen());
        Intent intent = PlayUtils.rateIntent(activity.getPackageName());
        Matcher<Intent> expectedIntent = allOf(hasAction(intent.getAction()), hasData(intent.getData()));
        clickForIntent(withId(R.id.rate_btn), expectedIntent);
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
        setScreen(newScreen());
        pressBack();
        checkDisplayed(MAIN_LAYOUT);
    }

    private View signOutBar() {
        return viewById(R.id.sign_out_bar);
    }

    private View signInBar() {
        return viewById(R.id.sign_in_bar);
    }

    private View reportProblem() {
        return viewById(R.id.report_problem);
    }

    private View vibrationContainer() {
        return viewById(R.id.vibration_container);
    }

    @NonNull
    private Matcher<View> vibrationButton() {
        return withId(R.id.vibration_btn);
    }

    @NonNull
    private Matcher<View> soundButton() {
        return withId(R.id.sound_btn);
    }

    private void hasVibration() {
        when(vibratorFacade.hasVibrator()).thenReturn(true);
    }

    private void vibrationAbsent() {
        when(vibratorFacade.hasVibrator()).thenReturn(false);
    }

    private void setVibration(boolean on) {
        when(settings().isVibrationOn()).thenReturn(on);
    }

    private void setSound(boolean on) {
        when(settings().isSoundOn()).thenReturn(on);
    }

}
