package com.ivygames.morskoiboi.main;

import android.content.Intent;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.playButton;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


public class MainScreenTest extends MainScreen_ {

    @Override
    public BattleshipScreen newScreen() {
        return new MainScreen(activity);
    }

    public void TestNoAds() {
        // TODO:
    }

    @Test
    public void when_billing_available__no_ads_button_visible() {
        setBillingAvailable(true);
        showScreen();
        assertThat(viewById(R.id.no_ads).getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_billing_NOT_available__no_ads_button_gone() {
        setBillingAvailable(false);
        showScreen();
        assertThat(viewById(R.id.no_ads).getVisibility(), is(not(View.VISIBLE)));
    }

    @Test
    public void when_billing_NOT_available__no_ads_button_gone_even_after_getting_to_another_screen_and_back() {
        setBillingAvailable(false);
        showScreen();
        assertThat(viewById(R.id.no_ads).getVisibility(), is(not(View.VISIBLE)));
        onView(withId(R.id.help)).perform(click());
        pressBack();
        assertThat(viewById(R.id.no_ads).getVisibility(), is(not(View.VISIBLE)));
    }

    @Test
    public void when_play_button_is_pressed__select_game_screen_opens() {
        clickOn(playButton());
        onView(SELECT_GAME_LAYOUT).check(matches(isDisplayed()));
    }

    @Test
    public void when_help_button_is_pressed__help_screen_opens() {
        onView(withId(R.id.help)).perform(click());
        onView(HELP_LAYOUT).check(matches(isDisplayed()));
    }

    @Test
    public void when_settings_button_is_pressed__settings_screen_opens() {
        onView(withId(R.id.settings_button)).perform(click());
        onView(SETTINGS_LAYOUT).check(matches(isDisplayed()));
    }

    @Test
    public void when_share_button_is_pressed__sharing_intent_is_fired() {
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_SEND), hasType("text/plain"));
        clickForIntent(withId(R.id.share_button), expectedIntent);
    }

    @Test
    public void when_signed_in__plus_one_button_is_displayed() {
        setSignedIn(true);
        showScreen();
        onView(pusOneButton()).check(matches(isDisplayed()));
    }

    @Test
    public void when_NOT_signed_in__plus_one_button_is_NOT_displayed() {
        setSignedIn(false);
        showScreen();
        onView(pusOneButton()).check(matches(not(isDisplayed())));
    }

//    @Test
    public void WhenPlus1ButtonPressed_but_ReconnectionRequired__ApiClientDisconnects() {
//        // TODO:
//        setSignedIn(true);
//        onView(pusOneButton()).perform(click());
//        ActivityResult result = new ActivityResult(GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED, null);
//        clickForIntent(pusOneButton(), anyIntent(), result);
//        verify(apiClient(), times(1)).disconnect();
    }
}
