package com.ivygames.morskoiboi.scenario;


import android.app.Instrumentation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.util.Log;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerFactory;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ScreenTestRule;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.player.DummyCallback;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.commons.logger.Ln;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static com.ivygames.morskoiboi.ScreenUtils.WIN_LAYOUT;
import static com.ivygames.morskoiboi.ScreenUtils.autoSetup;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.done;
import static com.ivygames.morskoiboi.ScreenUtils.lostScreen;
import static com.ivygames.morskoiboi.ScreenUtils.playButton;
import static com.ivygames.morskoiboi.ScreenUtils.vsAndroid;
import static com.ivygames.morskoiboi.ScreenUtils.waitId;

public class WinLostScenarioTest {
    private static final long WON_GAME_DELAY = 3000; // milliseconds
    private static final long LOST_GAME_DELAY = 5000; // milliseconds

    @Rule
    public ScreenTestRule rule = new ScreenTestRule();

    private volatile WinLostIdlingResource playResource;
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    @Before
    public void setup() {
        BattleshipActivity activity = rule.getActivity();
        Dependencies.inject(new GameSettings(activity));

        Rules rules = new RussianRules(new MyRandom());

        Dependencies.inject(rules);
        Dependencies.inject(new Placement(new MyRandom(), rules));

        IdlingPolicies.setMasterPolicyTimeout(20, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(20, TimeUnit.MINUTES);
    }

    @After
    public void teardown() {
        if (playResource != null) {
            unregisterIdlingResources(playResource);
        }
    }

    @Test
    public void WhenGameIsWon__WinScreenIsDisplayed() throws InterruptedException {
        Dependencies.inject(new BidPlayerFactory(2));
        Dependencies.inject(new BidAiPlayerFactory(1));

        goToGameplay();

        Shooter shooter = new Shooter(Dependencies.getRules(),
                Dependencies.getPlacement(),
                rule.getActivity().findViewById(R.id.enemy_board),
                (int) rule.getActivity().getResources().getDimension(R.dimen.ship_border));

        playResource = new WinLostIdlingResource();
        registerIdlingResources(playResource);

//        while (shooter.hasShots()) {
//            shooter.shoot();
//        }

//        registerIdlingResources(playResource);

//        Thread.sleep(WON_GAME_DELAY + 300);

        checkDisplayed(WIN_LAYOUT);
    }

    @Test
    public void WhenGameIsLost__LostScreenIsDisplayed() throws InterruptedException {
        Dependencies.inject(new BidPlayerFactory(1));
        Dependencies.inject(new BidAiPlayerFactory(2));

        goToGameplay();

        playResource = new WinLostIdlingResource();
        registerIdlingResources(playResource);

        Ln.i("TEST", "waiting");
        onView(isRoot()).perform(waitId(lostScreen(), LOST_GAME_DELAY + 1000));

        Ln.i("TEST", "assert");
        checkDisplayed(lostScreen());
    }

    private void goToGameplay() {
        clickOn(playButton());
        clickOn(vsAndroid());
        clickOn(autoSetup());
        clickOn(done());
    }

    private class BidPlayerFactory implements PlayerFactory {

        private final int mBid;

        public BidPlayerFactory(int bid) {
            mBid = bid;
        }

        @Override
        public PlayerOpponent createPlayer(@NonNull String name, @NonNull Placement placement, @NonNull Rules rules) {
            return new BidPlayer("player1", placement, rules, mBid, new WinLostCallback());
        }
    }

    private class WinLostCallback extends DummyCallback {

        @Override
        public void onWin() {
            Log.i("TEST", "win");
            playResource.setIdle();
        }

        @Override
        public void onLost(@Nullable Board board) {
            Log.i("TEST", "lost");
            playResource.setIdle();
        }

        @Override
        public void onPlayersTurn() {
//            playResource.setIdle();
        }
    }

}
