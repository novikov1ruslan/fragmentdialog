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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static com.ivygames.morskoiboi.ScreenUtils.WIN_LAYOUT;
import static com.ivygames.morskoiboi.ScreenUtils.autoSetup;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.done;
import static com.ivygames.morskoiboi.ScreenUtils.lostScreen;
import static com.ivygames.morskoiboi.ScreenUtils.playButton;
import static com.ivygames.morskoiboi.ScreenUtils.vsAndroid;
import static com.ivygames.morskoiboi.ScreenUtils.waitFor;

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

        playResource = new WinLostIdlingResource();
        registerIdlingResources(playResource);
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

        log("ready to shoot");
        while (shooter.hasShots()) {
            playResource.setIdle(false);
            espressoWait();
            shooter.shoot();
        }

        playResource.setIdle(false);
        espressoWait();
        waitFor(WIN_LAYOUT, WON_GAME_DELAY + 1000);
    }

    private void espressoWait() throws InterruptedException {
        while (!playResource.isIdleNow()) {
            Thread.sleep(1000);
            log("sleeping");
        }
    }

    @Test
    public void WhenGameIsLost__LostScreenIsDisplayed() throws InterruptedException {
        Dependencies.inject(new BidPlayerFactory(1));
        Dependencies.inject(new BidAiPlayerFactory(2));

        goToGameplay();
        playResource.setIdle(false);
        espressoWait();

        waitFor(lostScreen(), LOST_GAME_DELAY + 1000);
    }

    private void goToGameplay() {
        clickOn(playButton());
        clickOn(vsAndroid());
        clickOn(autoSetup());
        clickOn(done());
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                log("main thread is idle");
            }
        });
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
            log("win");
            playResource.setIdle(true);
        }

        @Override
        public void onLost(@Nullable Board board) {
            log("lost");
            playResource.setIdle(true);
        }

        @Override
        public void onPlayersTurn() {
            log("turn");
            playResource.setIdle(true);
        }
    }

    private static void log(String msg) {
        Log.i("TEST", msg);
    }

}
