package com.ivygames.morskoiboi.scenario;


import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingPolicies;

import com.ivygames.morskoiboi.AiPlayerFactory;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerFactory;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ScreenTest;
import com.ivygames.morskoiboi.ScreenTestRule;
import com.ivygames.morskoiboi.player.AiOpponent;
import com.ivygames.morskoiboi.player.DummyCallback;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.variant.RussianRules;

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
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.done;
import static com.ivygames.morskoiboi.ScreenUtils.playButton;
import static com.ivygames.morskoiboi.ScreenUtils.vsAndroid;

public class WinLostScenarioTest extends ScreenTest {
    @Rule
    public ScreenTestRule rule = new ScreenTestRule();

    private final WinIdlingResource playResource = new WinIdlingResource();
//    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    private class MyRandom extends Random {
        boolean b;

        @Override
        public int nextInt() {
            int i = b ? 0 : 1;
            b = !b;
            return i;
        }

        @Override
        public int nextInt(int n) {
            if (n > 1) {
                return nextInt();
            }

            return 0;
        }
    }

    @Before
    public void setup() {
        Rules rules = new RussianRules(new MyRandom());
        Placement placement = new Placement(new MyRandom(), rules);

        Dependencies.inject(rules);
        Dependencies.inject(placement);
        Dependencies.inject(new GameSettings(rule.getActivity()));

        IdlingPolicies.setMasterPolicyTimeout(20, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(20, TimeUnit.MINUTES);
    }

    @After
    public void teardown() {
        unregisterIdlingResources(playResource);
    }

    @Test
    public void WhenGameIsWon__WinScreenIsDisplayed() {
        PlayerFactory playerFactory = new MyPlayerFactory();
        AiPlayerFactory aiPlayerFactory = new MyAiPlayerFactory();
        Dependencies.inject(playerFactory);
        Dependencies.inject(aiPlayerFactory);

        clickOn(playButton());
        clickOn(vsAndroid());
        clickOn(autoSetup());
        clickOn(done());

        registerIdlingResources(playResource);
//        instrumentation.waitForIdleSync();

        checkDisplayed(WIN_LAYOUT);
    }

    @Override
    protected BattleshipScreen newScreen() {
        return null;
    }

    private class MyPlayerFactory implements PlayerFactory {

        @Override
        public PlayerOpponent createPlayer(@NonNull String name, @NonNull Placement placement, @NonNull Rules rules) {
            return new BidPlayer("winner", placement, rules, 2, new MyCallback());
        }

        private class MyCallback extends DummyCallback {
            @Override
            public void onWin() {
                playResource.win();
            }
        }
    }

    private static class MyAiPlayerFactory implements AiPlayerFactory {

        @Override
        public AiOpponent createPlayer(@NonNull String name,
                                       @NonNull Placement placement,
                                       @NonNull Rules rules) {
            return new BidAiOpponent("looser", placement, rules, 1);
        }
    }

    private static class BidAiOpponent extends AiOpponent {

        private final int mBid;

        public BidAiOpponent(@NonNull String name,
                             @NonNull Placement placement,
                             @NonNull Rules rules, int bid) {
            super(name, placement, rules);
            mBid = bid;
        }

        @Override
        public void startBidding(int bid) {
            super.startBidding(mBid);
        }
    }

}
