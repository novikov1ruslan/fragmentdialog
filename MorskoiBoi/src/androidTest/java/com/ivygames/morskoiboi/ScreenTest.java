package com.ivygames.morskoiboi;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.Intents;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothLayout;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupLayout;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListLayout;
import com.ivygames.morskoiboi.screen.help.HelpLayout;
import com.ivygames.morskoiboi.screen.internet.InternetGameLayout;
import com.ivygames.morskoiboi.screen.main.MainScreenLayout;
import com.ivygames.morskoiboi.screen.ranks.RanksLayout;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout;
import com.ivygames.morskoiboi.screen.settings.SettingsLayout;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public abstract class ScreenTest {

    protected static final Matcher<View> MAIN_LAYOUT = instanceOf(MainScreenLayout.class);
    protected static final Matcher<View> HELP_LAYOUT = instanceOf(HelpLayout.class);
    protected static final Matcher<View> SETTINGS_LAYOUT = instanceOf(SettingsLayout.class);
    protected static final Matcher<View> SELECT_GAME_LAYOUT = instanceOf(SelectGameLayout.class);
    protected static final Matcher<View> BLUETOOTH_LAYOUT = instanceOf(BluetoothLayout.class);
    protected static final Matcher<View> DEVICE_LIST_LAYOUT = instanceOf(DeviceListLayout.class);
    protected static final Matcher<View> RANKS_LAYOUT = instanceOf(RanksLayout.class);
    protected static final Matcher<View> BOARD_SETUP_LAYOUT = instanceOf(BoardSetupLayout.class);
    protected static final Matcher<View> INTERNET_GAME_LAYOUT = instanceOf(InternetGameLayout.class);

    @Rule
    public ScreenTestRule rule = new ScreenTestRule();

    private BattleshipActivity activity;
    private TaskResource setScreenResource;
    private TaskResource signInSucceeded;
    private GoogleApiClientWrapper apiClient;
    private AndroidDevice androidDevice;
    private BattleshipScreen screen;

    public abstract BattleshipScreen newScreen();

    public void setup() {
        activity = rule.getActivity();
        apiClient = rule.getApiClient();
        androidDevice = rule.getAndroidDevice();
    }

    @After
    public void teardown() {
        if (setScreenResource != null) {
            Espresso.unregisterIdlingResources(setScreenResource);
        }

        if (signInSucceeded != null) {
            Espresso.unregisterIdlingResources(signInSucceeded);
        }
    }

    protected void setBillingAvailable(boolean isAvailable) {
        when(androidDevice.isBillingAvailable()).thenReturn(isAvailable);
    }

    protected void setSignedIn(boolean signedIn) {
        when(apiClient.isConnected()).thenReturn(signedIn);
        setScreen(newScreen());
    }

    protected final BattleshipScreen screen() {
        return screen;
    }

    protected void setScreen(final BattleshipScreen screen) {
        this.screen = screen;
        setScreenResource = new TaskResource(new Runnable() {
            @Override
            public void run() {
                activity.setScreen(screen);
            }
        });
        Espresso.registerIdlingResources(setScreenResource);
    }

    protected void signInSucceeded(final SignInListener listener) {
        signInSucceeded = new TaskResource(new Runnable() {
            @Override
            public void run() {
                listener.onSignInSucceeded();
            }
        });
        Espresso.registerIdlingResources(signInSucceeded);
    }

    protected static void clickForIntent(Matcher<View> view, Matcher<Intent> expectedIntent) {
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(0, null);
        clickForIntent(view, expectedIntent, result);
    }

    protected static void clickForIntent(Matcher<View> view,
                                         Matcher<Intent> expectedIntent,
                                         Instrumentation.ActivityResult result) {
        Intents.init();
        try {
            intending(expectedIntent).respondWith(result);
            onView(view).perform(click());
            intended(expectedIntent);
        } finally {
            Intents.release();
        }
    }

    protected final View viewById(int id) {
        return activity.findViewById(id);
    }

    protected final BattleshipActivity activity() {
        return activity;
    }

    protected final GoogleApiClientWrapper apiClient() {
        return apiClient;
    }

    protected final AndroidDevice device() {
        return androidDevice;
    }

    protected static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    protected void checkDisplayed(Matcher<View> view) {
        onView(view).check(matches(isDisplayed()));
    }
}
