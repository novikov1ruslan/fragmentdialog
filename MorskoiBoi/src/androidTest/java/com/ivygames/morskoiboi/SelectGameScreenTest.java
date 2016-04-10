package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothLayout;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupLayout;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListLayout;
import com.ivygames.morskoiboi.screen.internet.InternetGameLayout;
import com.ivygames.morskoiboi.screen.main.MainScreenLayout;
import com.ivygames.morskoiboi.screen.ranks.RanksLayout;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
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
        onView(Matchers.<View>instanceOf(MainScreenLayout.class)).check(matches(isDisplayed()));
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
        onView(Matchers.<View>instanceOf(RanksLayout.class)).check(matches(isDisplayed()));
    }

    @Test
    public void when_vs_android_is_pressed__board_setup_screen_opens() {
        setScreen(newScreen());
        onView(withId(R.id.vs_android)).perform(click());
        onView(Matchers.<View>instanceOf(BoardSetupLayout.class)).check(matches(isDisplayed()));
    }

    @Test
    public void when_BT_button_is_pressed_and_BT_enabled__BT_screen_opens() {
        when(device().hasBluetooth()).thenReturn(true);
        when(device().bluetoothEnabled()).thenReturn(true);
        setScreen(newScreen());
        onView(viaBtButton()).perform(click());
        onView(Matchers.<View>instanceOf(BluetoothLayout.class)).check(matches(isDisplayed()));
    }

    @Test
    public void when_BT_button_is_pressed_and_BT_NOT_enabled__BT_screen_not_displayed() {
        when(device().hasBluetooth()).thenReturn(true);
        when(device().bluetoothEnabled()).thenReturn(false);
        BattleshipScreen screen = newScreen();
        setScreen(screen);
        onView(viaBtButton()).perform(click());
        onView(Matchers.<View>instanceOf(BluetoothLayout.class)).check(doesNotExist());
        onView(withText(R.string.bluetooth_not_available)).check(matches(isDisplayed()));
    }

    @Test
    public void when_internet_button_pressed__internet_game_screen_opens() {
        setSignedIn(true);
        onView(internetButton()).perform(click());
        onView(Matchers.<View>instanceOf(InternetGameLayout.class)).check(matches(isDisplayed()));
    }

    @Test
    public void when_internet_button_pressed_but_NOT_signed_in__sign_in_dialog_opens() {
        setSignedIn(false);
        onView(internetButton()).perform(click());
        onView(withText(R.string.internet_request)).check(matches(isDisplayed()));
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
