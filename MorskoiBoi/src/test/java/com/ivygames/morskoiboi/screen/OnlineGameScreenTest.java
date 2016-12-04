package com.ivygames.morskoiboi.screen;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.RussianRules;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.common.ui.Screen;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class OnlineGameScreenTest {

    @Mock
    private BattleshipActivity activity;
    @Mock
    private Game game;
    @Mock
    private RealTimeMultiplayer mMultiplayer;
    private Rules mRules = new RussianRules();

    private Session session;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        session = new Session(mock(PlayerOpponent.class), mock(Opponent.class));
        FragmentManager fm = mock(FragmentManager.class);
        when(activity.getFragmentManager()).thenReturn(fm);
        when(fm.beginTransaction()).thenReturn(mock(FragmentTransaction.class));

        Dependencies.inject(mMultiplayer);
        Dependencies.inject(mRules);
    }

    @Test
    public void WhenConnectionIsLostDuringBoardSetup__GameGinishes() {
        OnlineGameScreen screen = new BoardSetupScreen(activity, game, session);

        screen.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);

        verify(game, times(1)).finish();
    }

//    @Test
//    public void WhenConnectionIsLostDuringGameplay__GameGinishes() {
//        OnlineGameScreen screen = new GameplayScreen(activity, game, session, mock(TurnTimerController.class));
//
//        screen.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);
//
//        verify(game, times(1)).finish();
//    }

}