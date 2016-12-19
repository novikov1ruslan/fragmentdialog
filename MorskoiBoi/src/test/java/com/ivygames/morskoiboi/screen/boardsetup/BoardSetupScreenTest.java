package com.ivygames.morskoiboi.screen.boardsetup;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BoardSetupScreenTest {
    @Mock
    private Game game;

    private OnlineGameScreen screen;

    private BattleshipActivity activity;
    private PlayerOpponent player;
    private Rules rules;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        rules = Dependencies.getRules();
        player = new PlayerOpponent("player", rules.getAllShipsSizes().length);
        Session session = new Session(player, mock(Opponent.class));
        activity = Robolectric.setupActivity(BattleshipActivity.class);
        screen = new BoardSetupScreen(activity, game, session);
    }

    @Test
    public void WhenAutoPressed__BoardIsSet() {
        activity.setScreen(screen);

        activity.findViewById(R.id.auto_setup).performClick();

        assertThat(BoardUtils.isBoardSet(player.getBoard(), rules), is(true));
    }

}