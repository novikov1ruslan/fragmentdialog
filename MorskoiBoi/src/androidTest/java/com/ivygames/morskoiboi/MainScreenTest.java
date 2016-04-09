package com.ivygames.morskoiboi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.help.HelpLayout;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout;
import com.ivygames.morskoiboi.screen.settings.SettingsLayout;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


public class MainScreenTest extends ScreenTest {

    @Before
    public void startup() {
        super.setup();
    }

    @Override
    public BattleshipScreen screen() {
        return new MainScreen(activity(), apiClient());
    }

    @Test
    public void when_play_button_is_pressed__select_game_screen_opens() {
        onView(withId(R.id.play)).perform(click());
        onView(Matchers.<View>instanceOf(SelectGameLayout.class)).check(matches(isDisplayed()));
    }

    @Test
    public void when_help_button_is_pressed__help_screen_opens() {
        onView(withId(R.id.help)).perform(click());
        onView(Matchers.<View>instanceOf(HelpLayout.class)).check(matches(isDisplayed()));
    }

    @Test
    public void when_settings_button_is_pressed__settings_screen_opens() {
        onView(withId(R.id.settings_button)).perform(click());
        onView(Matchers.<View>instanceOf(SettingsLayout.class)).check(matches(isDisplayed()));
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
//    public void when_plus_one_button_is_pressed__plus_one_intent_is_fired() {
//        onView(withId(R.id.plus_one_button)).perform(click());
//        // TODO:
//    }
//
    @Test
    public void when_achievements_button_is_pressed_when_NOT_signed_in__sign_in_dialog_displayed() {
        setSignedIn(false);
        onView(achievementsButton()).perform(click());
        onView(withText(R.string.achievements_request)).check(matches(isDisplayed()));
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
