package com.ivygames.morskoiboi;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.ui.SignInListener;
import com.ivygames.morskoiboi.idlingresources.TaskResource;
import com.ivygames.morskoiboi.matchers.DrawableMatcher;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothLayout;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListLayout;
import com.ivygames.morskoiboi.screen.gameplay.GameplayLayoutInterface;
import com.ivygames.morskoiboi.screen.help.HelpLayout;
import com.ivygames.morskoiboi.screen.internet.InternetGameLayout;
import com.ivygames.morskoiboi.screen.main.MainScreenLayout;
import com.ivygames.morskoiboi.screen.ranks.RanksLayout;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout;
import com.ivygames.morskoiboi.screen.settings.SettingsLayout;

import org.apache.commons.collections4.set.UnmodifiableSet;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public abstract class ScreenTest {

    protected static final Matcher<View> MAIN_LAYOUT = instanceOf(MainScreenLayout.class);
    protected static final Matcher<View> HELP_LAYOUT = instanceOf(HelpLayout.class);
    protected static final Matcher<View> SETTINGS_LAYOUT = instanceOf(SettingsLayout.class);
    protected static final Matcher<View> SELECT_GAME_LAYOUT = instanceOf(SelectGameLayout.class);
    protected static final Matcher<View> BLUETOOTH_LAYOUT = instanceOf(BluetoothLayout.class);
    protected static final Matcher<View> DEVICE_LIST_LAYOUT = instanceOf(DeviceListLayout.class);
    protected static final Matcher<View> RANKS_LAYOUT = instanceOf(RanksLayout.class);
    protected static final Matcher<View> INTERNET_GAME_LAYOUT = instanceOf(InternetGameLayout.class);
    protected static final Matcher<View> GAMEPLAY_LAYOUT = instanceOf(GameplayLayoutInterface.class);

    @Rule
    public ScreenTestRule rule = new ScreenTestRule();

    protected BattleshipActivity activity;
    private TaskResource setScreenResource;
    private TaskResource signInSucceeded;
    private TaskResource pause;
    private TaskResource resume;
    private TaskResource destroy;
    private ThrowingApiClient apiClient;
    private AndroidDevice androidDevice;
    private BattleshipScreen screen;

    protected final Set<String> INVITATIONS = new HashSet<>();
    protected final Set<String> NO_INVITATIONS = UnmodifiableSet.unmodifiableSet(Collections.<String>emptySet());

    protected abstract BattleshipScreen newScreen();

    @Before
    public void setup() {
        activity = rule.getActivity();
        apiClient = rule.getApiClient();
        androidDevice = rule.getDevice();
        setProgress(0);
        INVITATIONS.add("id1");
        INVITATIONS.add("id2");
    }

    @After
    public void teardown() {
        if (setScreenResource != null) {
            unregisterIdlingResources(setScreenResource);
        }

        if (signInSucceeded != null) {
            unregisterIdlingResources(signInSucceeded);
        }

        if (pause != null) {
            unregisterIdlingResources(pause);
        }

        if (resume != null) {
            unregisterIdlingResources(resume);
        }

        if (destroy != null) {
            unregisterIdlingResources(destroy);
        }
    }

    protected final void setBillingAvailable(boolean isAvailable) {
        when(androidDevice.isBillingAvailable()).thenReturn(isAvailable);
    }

    protected final void setCanResolveIntent(boolean can) {
        when(androidDevice.canResolveIntent(any(Intent.class))).thenReturn(can);
    }

    protected void setSignedIn(boolean signedIn) {
        if (signedIn) {
            apiClient.connect();
        } else {
            apiClient.disconnect();
        }
    }

    protected final BattleshipScreen screen() {
        return screen;
    }

    protected final void showScreen() {
        setScreen(newScreen());
    }

    protected void setScreen(final BattleshipScreen screen) {
        this.screen = screen;
        setScreenResource = new TaskResource(new Runnable() {
            @Override
            public void run() {
                activity.setScreen(screen);
            }
        });
        registerIdlingResources(setScreenResource);
    }

    protected void signInSucceeded(final SignInListener listener) {
        signInSucceeded = new TaskResource(new Runnable() {
            @Override
            public void run() {
                listener.onSignInSucceeded();
            }
        });
        registerIdlingResources(signInSucceeded);
    }

    protected final void resume() {
        resume = new TaskResource(new Runnable() {
            @Override
            public void run() {
                activity.onResume();
            }
        });
        registerIdlingResources(resume);
    }

    protected final void pause() {
        pause = new TaskResource(new Runnable() {
            @Override
            public void run() {
                activity.onPause();
            }
        });
        registerIdlingResources(pause);
    }

    protected final void destroy() {
        destroy = new TaskResource(new Runnable() {
            @Override
            public void run() {
                activity.onDestroy();
            }
        });
        registerIdlingResources(destroy);
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

    protected final AndroidDevice device() {
        return androidDevice;
    }

    protected final GameSettings settings() {
        return rule.settings();
    }

    protected static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    protected final void checkNotDisplayed(Matcher<View> view) {
        onView(view).check(matches(not(isDisplayed())));
    }

    protected final void checkDoesNotExist(Matcher<View> view) {
        onView(view).check(doesNotExist());
    }

    protected final void setProgress(int progress) {
        when(settings().getProgress()).thenReturn(progress);
    }

    protected final String getString(int id, String text) {
        return activity.getString(id, text);
    }

    protected final String getString(int id) {
        return activity.getString(id);
    }

    protected final Matcher<Intent> fromIntent(Intent intent) {
        Matcher<Intent> action = hasAction(intent.getAction());
        Matcher<Intent> data = hasData(intent.getData());
        Matcher<Intent> type = hasType(intent.getType());
        return allOf(action, data, type);
    }

    @NonNull
    protected Matcher<View> signInButton() {
        return withText(R.string.sign_in);
    }

    @NonNull
    protected Matcher<View> cancelButton() {
        return withText(R.string.cancel);
    }

    @NonNull
    protected Matcher<View> okButton() {
        return withText(R.string.ok);
    }

    @NonNull
    protected final Matcher<View> positiveButton() {
        return ViewMatchers.withId(R.id.positive_button);
    }

    @NonNull
    protected final Matcher<View> negativeButton() {
        return ViewMatchers.withId(R.id.negative_button);
    }

    protected final void verifyConnected() {
        assertThat(apiClient.isConnected(), is(true));
    }

    protected final void verifyDisconnected() {
        assertThat(apiClient.isConnected(), is(false));
    }

    protected final void verifyAchievementsShown() {
        assertThat(apiClient.achievementsShown(), is(true));
    }

    protected final void verifyLeaderboardsShown() {
        assertThat(apiClient.leaderboardsShown(), is(true));    }

    protected final void expectSubmitScoreBeCalled(boolean called) {
        assertThat(apiClient.submitScoreBeCalled(), is(called));
    }

    public void onActivityResult(int request, int result, Intent data) {
        activity.onActivityResult(request, result, data);
    }
}
