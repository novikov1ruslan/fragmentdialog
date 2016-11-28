package com.ivygames.morskoiboi.component;

import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.battleship.ChatMessage;
import com.ivygames.morskoiboi.player.ChatListener;
import com.ivygames.morskoiboi.russian.RussianRules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Random;

@RunWith(RobolectricTestRunner.class)
public class ScenarioTests {

    private Rules rules = new RussianRules();
    private Placement placement = new Placement(new Random(), rules.allowAdjacentShips());

    private ChatListener listener = new ChatListener() {
        @Override
        public void showChatCrouton(ChatMessage message) {

        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ExceptionHandler.setDryRun(true);
    }

    @Test
    public void foo() {
////        DelayedOpponent delegate = new DelayedOpponent();
//        AiOpponent android = new AiOpponent("Android", placement, rules);
//        android.setBoard(new Board());
//        PlayerOpponent player = new AiOpponent("Player", placement, rules);
//        player.setChatListener(listener);
////        delegate.setOpponent(player);
//        player.setOpponent(android);
////        android.setOpponent(delegate);
//        android.setOpponent(player);
//
//        player.startBidding(1);
    }
}
