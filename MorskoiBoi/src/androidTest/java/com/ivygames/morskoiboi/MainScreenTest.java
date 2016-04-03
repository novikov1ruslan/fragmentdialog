package com.ivygames.morskoiboi;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.ivygames.morskoiboi.screen.help.HelpLayout;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout;
import com.ivygames.morskoiboi.screen.settings.SettingsLayout;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


//@RunWith(AndroidJUnit4.class)
//@LargeTest
public class MainScreenTest {

    @Rule
    public ActivityTestRule<BattleshipActivity> rule = new ActivityTestRule<>(
            BattleshipActivity.class);

    private GoogleApiClientWrapper apiClient;

    private IdlingResource idlingResource;

    @Before
    public void startup() {
//        MockitoAnnotations.initMocks(this);
//        GoogleApiFactory.inject(apiClient);
//        Intents.init();
//        DeviceUtils.init(apiAvailability);
        final BattleshipActivity activity = rule.getActivity();
//        apiClient = activity.getApiClient();
        apiClient = mock(GoogleApiClientWrapper.class);
        idlingResource = new ScreenSetterResource(new Runnable() {
            @Override
            public void run() {
                activity.setScreen(new MainScreen(activity, apiClient));
            }
        });
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void teardown() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
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

//    @Test
//    public void when_share_button_is_pressed__sharing_intent_is_fired() {
////        Intents.init();
////        Matcher<Intent> intentMatcher = allOf(hasAction(Intent.ACTION_SEND), hasType("text/plain"));
//////        intending(intentMatcher).respondWith(null);
////        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
////        onView(withId(R.id.share_button)).perform(click());
////        intended(intentMatcher);
////        Intents.release();
//    }

//    @Test
//    public void when_plus_one_button_is_pressed__plus_one_intent_is_fired() {
//        onView(withId(R.id.plus_one_button)).perform(click());
//        // TODO:
//    }
//
    @Test
    public void when_achievements_button_is_pressed_when_NOT_signed_in__sign_in_dialog_displayed() {
//        Intent intent = Games.Achievements.getAchievementsIntent(mApiClient);//, BattleshipActivity.RC_UNUSED);
        when(apiClient.isConnected()).thenReturn(false);
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

}
