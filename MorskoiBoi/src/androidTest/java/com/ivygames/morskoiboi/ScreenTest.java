package com.ivygames.morskoiboi;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static org.mockito.Mockito.when;

public abstract class ScreenTest {

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

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }
}
