package com.ivygames.morskoiboi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MainScreenTest extends ScreenTest {

    private GameSettings settings;

    @Before
    public void startup() {
        settings = mock(GameSettings.class);
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new MainScreen(activity(), apiClient(), settings);
    }

    @Test
    public void TestNoAds() {
        // TODO:
    }

    @Test
    public void when_billing_available__no_ads_button_visible() {
        setBillingAvailable(true);
        setScreen(newScreen());
        assertThat(viewById(R.id.no_ads).getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void when_billing_NOT_available__no_ads_button_gone() {
        setBillingAvailable(false);
        setScreen(newScreen());
        assertThat(viewById(R.id.no_ads).getVisibility(), is(not(View.VISIBLE)));
    }

    @Test
    public void when_billing_NOT_available__no_ads_button_gone_even_after_getting_to_another_screen_and_back() {
        setBillingAvailable(false);
        setScreen(newScreen());
        assertThat(viewById(R.id.no_ads).getVisibility(), is(not(View.VISIBLE)));
        onView(withId(R.id.help)).perform(click());
        pressBack();
        assertThat(viewById(R.id.no_ads).getVisibility(), is(not(View.VISIBLE)));
    }

    @Test
    public void RateDialogShown() {
        when(settings.shouldProposeRating()).thenReturn(true);
        setScreen(newScreen());
        checkDisplayed(withText(R.string.rate_request));
    }

    @Test
    public void RateDialogNotShown() {
        when(settings.shouldProposeRating()).thenReturn(false);
        setScreen(newScreen());
        checkNotDisplayed(withText(R.string.rate_request));
    }

//    @Test
//    public void WhenThereIsInvitation__EnvelopeIsShown() {
//        when(invitationManager().hasInvitation()).thenReturn(true);
//        setScreen(newScreen());
//        InvitationButton button = (InvitationButton) activity().findViewById(R.id.play);
//        verify(button, times(1)).showInvitation();
//    }
//
//    @Test
//    public void WhenThereAreNoInvitations__EnvelopeIsHidden() {
//        when(invitationManager().hasInvitation()).thenReturn(false);
//        setScreen(newScreen());
//        InvitationButton button = (InvitationButton) activity().findViewById(R.id.play);
//        verify(button, times(1)).hideInvitation();
//    }

    @Test
    public void when_play_button_is_pressed__select_game_screen_opens() {
        onView(withId(R.id.play)).perform(click());
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
        onView(pusOneButton()).check(matches(isDisplayed()));
    }

    @Test
    public void when_NOT_signed_in__plus_one_button_is_NOT_displayed() {
        setSignedIn(false);
        onView(pusOneButton()).check(matches(not(isDisplayed())));
    }

//    @Test
//    public void WhenPlus1ButtonPressed_but_ReconnectionRequired__ApiClientDisconnects() {
//        // TODO:
//        setSignedIn(true);
//        onView(pusOneButton()).perform(click());
//        ActivityResult result = new ActivityResult(GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED, null);
//        clickForIntent(pusOneButton(), anyIntent(), result);
//        verify(apiClient(), times(1)).disconnect();
//    }

    @Test
    public void when_achievements_button_is_pressed_when_NOT_signed_in__sign_in_dialog_displayed() {
        setSignedIn(false);
        onView(achievementsButton()).perform(click());
        checkDisplayed(withText(R.string.achievements_request));
    }

    @Test
    public void when_leader_board_button_is_pressed_when_NOT_signed_in__sign_in_dialog_displayed() {
        setSignedIn(false);
        onView(leaderboardButton()).perform(click());
        onView(withText(R.string.leaderboards_request)).check(matches(isDisplayed()));
    }

    @Test
    public void when_achievements_button_is_pressed_when_signed_in__achievements_intent_is_fired() {
        Intent intent = new Intent();
        String expectedType = "expected type";
        intent.setType(expectedType);
        when(apiClient().getAchievementsIntent()).thenReturn(intent);
        setSignedIn(true);
        clickForIntent(achievementsButton(), hasType(expectedType));
    }

    @Test
    public void when_leader_board_button_is_pressed_when_signed_in__leader_board_intent_is_fired() {
        Intent intent = new Intent();
        String expectedType = "expected type";
        intent.setType(expectedType);
        when(apiClient().getLeaderboardIntent(anyString())).thenReturn(intent);
        setSignedIn(true);
        clickForIntent(leaderboardButton(), hasType(expectedType));
    }

    @NonNull
    private Matcher<View> leaderboardButton() {
        return withId(R.id.high_score);
    }

    @NonNull
    private Matcher<View> achievementsButton() {
        return withId(R.id.achievements_button);
    }

    @NonNull
    private Matcher<View> pusOneButton() {
        return withId(R.id.plus_one_button);
    }

}
