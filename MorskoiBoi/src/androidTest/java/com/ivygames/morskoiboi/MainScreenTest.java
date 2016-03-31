package com.ivygames.morskoiboi;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.ivygames.morskoiboi.screen.help.HelpLayout;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout;
import com.ivygames.morskoiboi.screen.settings.SettingsLayout;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainScreenTest {

//    @Mock
    private GoogleApiClient apiClient;

    @Rule
    public ActivityTestRule<BattleshipActivity> rule = new ActivityTestRule<>(
            BattleshipActivity.class);

    @Before
    @UiThreadTest
    public void startup() {
//        MockitoAnnotations.initMocks(this);
//        Intents.init();
//        apiClient = Mockito.mock(GoogleApiClient.class);
        BattleshipActivity activity = rule.getActivity();
        activity.setScreen(new MainScreen(activity, activity.getApiClient()));
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
//        Intents.init();
//        Matcher<Intent> intentMatcher = allOf(hasAction(Intent.ACTION_SEND), hasType("text/plain"));
////        intending(intentMatcher).respondWith(null);
//        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
//        onView(withId(R.id.share_button)).perform(click());
//        intended(intentMatcher);
//        Intents.release();
    }

//    @Test
//    public void when_plus_one_button_is_pressed__plus_one_intent_is_fired() {
//        onView(withId(R.id.plus_one_button)).perform(click());
//        // TODO:
//    }
//
    @Test
    public void when_achievements_button_is_pressed_when_NOT_signed_in__sign_in_dialog_displayed() {
//        Intent intent = Games.Achievements.getAchievementsIntent(mApiClient);//, BattleshipActivity.RC_UNUSED);

        onView(withId(R.id.achievements_button)).perform(click());
        onView(withText(R.string.achievements_request)).check(matches(isDisplayed()));
    }
//
//    @Test
//    public void when_leader_boards_button_is_pressed_when_NOT_signed_in__sign_in_dialog_displayed() {
//        onView(withId(R.id.high_score)).perform(click());
//        // TODO:
//    }
//
//    @Test
//    public void when_achievements_button_is_pressed_when_signed_in__achievements_intent_is_fired() {
//        onView(withId(R.id.achievements_button)).perform(click());
//        // TODO:
//    }
//
//    @Test
//    public void when_leader_boards_button_is_pressed__leader_board_intent_is_fired() {
//        onView(withId(R.id.high_score)).perform(click());
//        // TODO:
//    }

    @After
    public void tearDown() {
        goBackN();
    }

    private void goBackN() {
//        final int N = 10; // how many times to hit back button
//        for (int i = 0; i < N; i++) {
//            Espresso.pressBack();
//        }
    }
}
