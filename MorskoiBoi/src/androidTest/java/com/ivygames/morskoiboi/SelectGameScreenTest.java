package com.ivygames.morskoiboi;

import android.app.Activity;
import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

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
import static org.mockito.Mockito.when;


public class SelectGameScreenTest extends ScreenTest {

    private static final java.lang.String TEST_NAME = "Sagi";

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new SelectGameScreen(activity(), settings());
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
        setScreen(newScreen());
        pressBack();
        checkDisplayed(MAIN_LAYOUT);
    }

    @Test
    public void proper_name_is_shown() {
        when(settings().getPlayerName()).thenReturn(TEST_NAME);
        setScreen(newScreen());
        onView(withId(R.id.player_name)).check(matches(withText(TEST_NAME)));
    }

    @Test
    public void proper_rank_is_shown() {
        Rank rank = Rank.CAPTAIN;
        setProgress(rank.getScore());
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

    @NonNull
    private Matcher<View> viaBtButton() {
        return withId(R.id.via_bluetooth);
    }

}
