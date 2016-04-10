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
    private ScreenSetterResource idlingResource;

    private GoogleApiClientWrapper apiClient;
    private AndroidDevice androidDevice;

    public abstract BattleshipScreen newScreen();

    public void setup() {
        activity = rule.getActivity();
        apiClient = rule.getApiClient();
        androidDevice = rule.getAndroidDevice();
    }

    @After
    public void teardown() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }

    protected void setBillingAvailable(boolean isAvailable) {
        when(androidDevice.isBillingAvailable()).thenReturn(isAvailable);
    }

    protected void setSignedIn(boolean signedIn) {
        when(apiClient.isConnected()).thenReturn(signedIn);
        setScreen(newScreen());
    }

    protected void setScreen(final BattleshipScreen screen) {
        idlingResource = new ScreenSetterResource(new Runnable() {
            @Override
            public void run() {
                activity.setScreen(screen);
            }
        });
        Espresso.registerIdlingResources(idlingResource);
    }

    protected static void clickForIntent(Matcher<View> view, Matcher<Intent> expectedIntent) {
        Intents.init();
        try {
            intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
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

}
