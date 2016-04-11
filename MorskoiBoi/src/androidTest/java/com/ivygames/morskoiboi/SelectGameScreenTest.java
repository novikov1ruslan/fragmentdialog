package com.ivygames.morskoiboi;

import android.app.Activity;
import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SelectGameScreenTest extends ScreenTest {

    private static final java.lang.String TEST_NAME = "Sagi";
    private GameSettings settings;

    @Before
    public void setup() {
        settings = mock(GameSettings.class);
        when(settings.getProgress()).thenReturn(new Progress(Rank.CAPTAIN.getScore()));
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new SelectGameScreen(activity(), settings);
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
        setScreen(newScreen());
        pressBack();
        checkDisplayed(MAIN_LAYOUT);
    }

    @Test
    public void if_needs_to_show_help__tutorial_shown() {
        when(settings.showProgressHelp()).thenReturn(true);
        setScreen(newScreen());
        onView(tutorial()).check(matches(isDisplayed()));
    }

    @Test
    public void if_no_need_to_show_help__tutorial_NOT_shown() {
        when(settings.showProgressHelp()).thenReturn(false);
        setScreen(newScreen());
        onView(tutorial()).check(doesNotExist());
    }

    @Test
    public void if_tutorial_NOT_dismissed__it_is_shown_again() {
        when(settings.showProgressHelp()).thenReturn(true);
        setScreen(newScreen());
        pressBack();
        verify(settings, never()).hideProgressHelp();
    }

    @Test
    public void if_tutorial_dismissed__it_is_not_shown_again() {
        when(settings.showProgressHelp()).thenReturn(true);
        setScreen(newScreen());
        onView(withId(R.id.got_it_button)).perform(click());
        verify(settings, times(1)).hideProgressHelp();
    }

    @Test
    public void when_help_button_pressed__tutorial_shown() {
        setScreen(newScreen());
        onView(withId(R.id.help_button)).perform(click());
        onView(tutorial()).check(matches(isDisplayed()));
    }

    @Test
    public void proper_name_is_shown() {
        when(settings.getPlayerName()).thenReturn(TEST_NAME);
        setScreen(newScreen());
        onView(withId(R.id.player_name)).check(matches(withText(TEST_NAME)));
    }

    @Test
    public void proper_rank_is_shown() {
        Rank rank = Rank.CAPTAIN;
        when(settings.getProgress()).thenReturn(new Progress(rank.getScore()));
        setScreen(newScreen());
        onView(withId(R.id.rank_text)).check(matches(withText(rank.getNameRes())));
        onView(withId(R.id.player_rank)).check(matches(withDrawable(rank.getBitmapRes())));
    }

    @Test
    public void if_device_has_BT__BT_button_shown() {
        when(device().hasBluetooth()).thenReturn(true);
        setScreen(newScreen());
        onView(viaBtButton()).check(matches(isDisplayed()));
    }

    @Test
    public void if_device_has_no_BT__BT_button_NOT_shown() {
        when(device().hasBluetooth()).thenReturn(false);
        setScreen(newScreen());
        onView(viaBtButton()).check(matches(not(isDisplayed())));
    }

    @Test
    public void when_rank_button_is_pressed__ranks_screen_opens() {
        setScreen(newScreen());
        onView(withId(R.id.player_rank)).perform(click());
        checkDisplayed(RANKS_LAYOUT);
    }

    @Test
    public void when_vs_android_is_pressed__board_setup_screen_opens() {
        setScreen(newScreen());
        onView(withId(R.id.vs_android)).perform(click());
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void when_BT_button_pressed_and_BT_enabled__BT_screen_opens() {
        when(device().hasBluetooth()).thenReturn(true);
        when(device().bluetoothEnabled()).thenReturn(true);
        setScreen(newScreen());
        onView(viaBtButton()).perform(click());
        checkDisplayed(BLUETOOTH_LAYOUT);
    }

    @Test
    public void when_BT_button_pressed_and_BT_NOT_enabled_but_activated__BT_screen_displayed() {
        when(device().hasBluetooth()).thenReturn(true);
        when(device().bluetoothEnabled()).thenReturn(false);
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        when(device().canResolveIntent(any(Intent.class))).thenReturn(true);
        setScreen(newScreen());

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, null);
        clickForIntent(viaBtButton(), hasAction(enableIntent.getAction()), result);
        checkDisplayed(BLUETOOTH_LAYOUT);
    }

    @Test
    public void when_BT_button_pressed_and_BT_NOT_enabled__BT_screen_not_displayed() {
        when(device().hasBluetooth()).thenReturn(true);
        when(device().bluetoothEnabled()).thenReturn(false);
//        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        when(device().canResolveIntent(any(Intent.class))).thenReturn(false);
        setScreen(newScreen());
        onView(viaBtButton()).perform(click());
        onView(BLUETOOTH_LAYOUT).check(doesNotExist());
        onView(withText(R.string.bluetooth_not_available)).check(matches(isDisplayed()));
    }

    @Test
    public void when_internet_button_pressed__internet_game_screen_opens() {
        setSignedIn(true);
        onView(internetButton()).perform(click());
        checkDisplayed(INTERNET_GAME_LAYOUT);
    }

    @Test
    public void when_internet_button_pressed_but_NOT_signed_in__sign_in_dialog_opens() {
        setSignedIn(false);
        onView(internetButton()).perform(click());
        onView(withText(R.string.internet_request)).check(matches(isDisplayed()));
    }

    @Test
    public void when_sign_in_pressed__internet_screen_displayed() {
        setSignedIn(false);
        onView(internetButton()).perform(click());
        onView(withText(R.string.sign_in)).perform(click());
        verify(apiClient(), times(1)).connect();
        signInSucceeded((SignInListener) screen());
        checkDisplayed(INTERNET_GAME_LAYOUT);
    }

    @NonNull
    private Matcher<View> internetButton() {
        return withId(R.id.via_internet);
    }

    @NonNull
    private Matcher<View> tutorial() {
        return withText(R.string.see_ranks);
    }

    @NonNull
    private Matcher<View> viaBtButton() {
        return withId(R.id.via_bluetooth);
    }

}
