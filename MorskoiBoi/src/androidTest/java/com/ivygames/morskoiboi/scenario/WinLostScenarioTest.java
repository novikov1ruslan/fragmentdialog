package com.ivygames.morskoiboi.scenario;


import android.app.Instrumentation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.util.Log;

import com.ivygames.battleship.PlayerFactory;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ScreenTestRule;
import com.ivygames.morskoiboi.player.DummyCallback;
import com.ivygames.morskoiboi.russian.RussianRules;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static com.ivygames.morskoiboi.ScreenUtils.WIN_LAYOUT;
import static com.ivygames.morskoiboi.ScreenUtils.autoSetup;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.clickOnEnemyCell;
import static com.ivygames.morskoiboi.ScreenUtils.done;
import static com.ivygames.morskoiboi.ScreenUtils.lostScreen;
import static com.ivygames.morskoiboi.ScreenUtils.playButton;
import static com.ivygames.morskoiboi.ScreenUtils.vsAndroid;
import static com.ivygames.morskoiboi.ScreenUtils.waitFor;
import static com.ivygames.morskoiboi.ScreenUtils.yesButton;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WinLostScenarioTest {
    private static final long WON_GAME_DELAY = 3000; // milliseconds
    private static final long LOST_GAME_DELAY = 5000; // milliseconds

    @Rule
    public ScreenTestRule rule = new ScreenTestRule();

    private volatile WinLostIdlingResource playResource;
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    private Random mRandom;

    @Before
    public void setup() {
        BattleshipActivity activity = rule.getActivity();
        Rules rules = new RussianRules();

        mRandom = mock(Random.class);
        when(mRandom.nextInt()).thenReturn(0);
        when(mRandom.nextInt(anyInt())).thenReturn(0);

        Dependencies.inject(mRandom);
        Dependencies.inject(new GameSettings(activity));
        Dependencies.inject(rules);

        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.MINUTES);

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
    public void WinWin() throws InterruptedException {
        Dependencies.inject(new BidPlayerFactory(2, 2));
        Dependencies.inject(new BidAiPlayerFactory(mRandom, 1, 1));

        goToGameplay();


        shootToWin();
        verifyWin();

        clickOn(yesButton());
        clickOn(autoSetup());
        clickOn(done());

        shootToWin();
        verifyWin();
    }

    @Test
    public void WinLost() throws InterruptedException {
        Dependencies.inject(new BidPlayerFactory(2, 1));
        Dependencies.inject(new BidAiPlayerFactory(mRandom, 1 ,2));

        goToGameplay();

        shootToWin();
        verifyWin();

        clickOn(yesButton());
        clickOn(autoSetup());
        clickOn(done());

        idleWait();
        verifyLost();
    }

    @Test
    public void LostLost() throws InterruptedException {
        Dependencies.inject(new BidPlayerFactory(1, 1));
        Dependencies.inject(new BidAiPlayerFactory(mRandom, 2 ,2));

        goToGameplay();

        idleWait();
        verifyLost();

        clickOn(yesButton());
        clickOn(autoSetup());
        clickOn(done());

        idleWait();
        verifyLost();
    }

    @Test
    public void LostWin() throws InterruptedException {
        Dependencies.inject(new BidPlayerFactory(1, 2));
        Dependencies.inject(new BidAiPlayerFactory(mRandom, 2 ,1));

        goToGameplay();

        // Just to catch crash
        clickOnEnemyCell(5, 5);
        clickOnEnemyCell(7, 7);

        idleWait();
        verifyLost();

        clickOn(yesButton());
        clickOn(autoSetup());
        clickOn(done());

        shootToWin();
        verifyWin();
    }

    private void idleWait() throws InterruptedException {
        playResource.setIdle(false);
        espressoWait();
    }

    private void verifyLost() {
        waitFor(lostScreen(), LOST_GAME_DELAY + 1000);
    }

    private class BidPlayerFactory implements PlayerFactory {

        private final int[] mBid;

        public BidPlayerFactory(int... bid) {
            mBid = bid;
        }

        @Override
        public PlayerOpponent createPlayer(@NonNull String name, int numberOfShips) {
            return new BidPlayer("player1", numberOfShips, mBid, new WinLostCallback());
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

    private void espressoWait() throws InterruptedException {
        while (!playResource.isIdleNow()) {
            Thread.sleep(1000);
            log("sleeping");
        }
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

    private void shootToWin() throws InterruptedException {
        Shooter shooter = newShooter();

        log("ready to shoot");
        while (shooter.hasShots()) {
            idleWait();
            shooter.shoot();
        }

        idleWait();
    }

    @NonNull
    private Shooter newShooter() {
        return new Shooter(Dependencies.getRules(),
                mRandom,
                rule.getActivity().findViewById(R.id.enemy_board),
                (int) rule.getActivity().getResources().getDimension(R.dimen.ship_border));
    }

    private void verifyWin() {
        waitFor(WIN_LAYOUT, WON_GAME_DELAY + 1000);
    }

    private static void log(String msg) {
        Log.i("TEST", msg);
    }

}
