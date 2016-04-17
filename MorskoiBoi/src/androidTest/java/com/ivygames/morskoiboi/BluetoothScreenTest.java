package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;


public class BluetoothScreenTest extends ScreenTest {

    private BluetoothAdapterWrapper adapter;

    @Before
    public void setup() {
        adapter = mock(BluetoothAdapterWrapper.class);
        super.setup();
    }

    @Override
    public BluetoothScreen newScreen() {
        return new BluetoothScreen(activity(), adapter);
    }

    @Test
    public void when_back_button_pressed__select_game_screen_opens() {
        setScreen(newScreen());
        doNothing().when(adapter).cancelDiscovery();
        pressBack();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    @Test
    public void when_join_game_pressed__device_list_screen_displayed() {
//        setScreen(newScreen());
//        onView(joinGameButton()).perform(click());
//        checkDisplayed(DEVICE_LIST_LAYOUT);
    }

    @Test
    public void when_create_game_pressed__layout_disabled() {
//        setScreen(newScreen());
//        onView(withId(R.id.create_game_btn)).perform(click());
//        onView(joinGameButton()).perform(click());
//        onView(DEVICE_LIST_LAYOUT).check(matches(is(not(isDisplayed()))));
    }

    @NonNull
    private Matcher<View> joinGameButton() {
        return withId(R.id.join_game_btn);
    }
}
