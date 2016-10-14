package com.ivygames.morskoiboi;

import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.player.AiOpponent;
import com.ivygames.morskoiboi.player.ChatListener;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Random;

@RunWith(RobolectricTestRunner.class)
public class ScenarioTests {

    private Rules rules = new RussianRules(new Random());
    private Placement placement = new Placement(new Random(), rules);

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
