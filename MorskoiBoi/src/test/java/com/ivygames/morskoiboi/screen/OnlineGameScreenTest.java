package com.ivygames.morskoiboi.screen;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.ScoreStatistics;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnlineGameScreenTest {

    private BattleshipActivity activity;
    @Mock
    private Game game;
    @Mock
    private RealTimeMultiplayer mMultiplayer;

    private Session session;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        session = new Session(mock(PlayerOpponent.class), mock(Opponent.class));
        activity = Robolectric.buildActivity(BattleshipActivity.class).get();

        Dependencies.inject(mMultiplayer);
    }

    @Test
    public void WhenConnectionIsLostDuringBoardSetup__GameFinishes() {
        OnlineGameScreen screen = new BoardSetupScreen(activity, game, session);

        screen.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);

        verify(game, times(1)).finish();
    }

    @Test
    public void WhenConnectionIsLostDuringGameplay__GameFinishes() {
        OnlineGameScreen screen = new GameplayScreen(activity, game, session, mock(TurnTimerController.class));

        screen.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);

        verify(game, times(1)).finish();
    }

    @Test
    public void WhenConnectionIsLostDuringLostScreen__GameFinishes() {
        OnlineGameScreen screen = new LostScreen(activity, game, session);

        screen.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);

        verify(game, times(1)).finish();
    }

    @Test
    public void WhenConnectionIsLostDuringWinScreen__GameFinishes() {
        Collection<Ship> fleet = new ArrayList<>();
        ScoreStatistics statistics = mock(ScoreStatistics.class);
        OnlineGameScreen screen = new WinScreen(activity, game, session, fleet, statistics, false);

        screen.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);

        verify(game, times(1)).finish();
    }

    @Test
    public void WhenOpponentLeavesDuringBoardSetup__GameFinishes() {
        OnlineGameScreen screen = new BoardSetupScreen(activity, game, session);

        screen.onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);

        verify(game, times(1)).finish();
    }

    @Test
    public void WhenOpponentLeavesDuringGameplay__GameFinishes() {
        OnlineGameScreen screen = new GameplayScreen(activity, game, session, mock(TurnTimerController.class));

        screen.onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);

        verify(game, times(1)).finish();
    }

    @Test
    public void WhenOpponentLeavesDuringLostScreen__GameFinishes() {
        OnlineGameScreen screen = new LostScreen(activity, game, session);

        screen.onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);

        verify(game, times(1)).finish();
    }

    @Test
    public void WhenOpponentLeavesDuringWinScreen__GameFinishes() {
        Collection<Ship> fleet = new ArrayList<>();
        ScoreStatistics statistics = mock(ScoreStatistics.class);
        OnlineGameScreen screen = new WinScreen(activity, game, session, fleet, statistics, false);

        screen.onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);

        verify(game, times(1)).finish();
    }
}