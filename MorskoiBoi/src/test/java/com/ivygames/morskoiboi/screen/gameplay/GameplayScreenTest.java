package com.ivygames.morskoiboi.screen.gameplay;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;

import com.ivygames.battleship.Rules;
import com.ivygames.battleship.RussianRules;
import com.ivygames.battleship.ai.AiOpponent;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;

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
public class GameplayScreenTest {
    @Mock
    private BattleshipActivity activity;
    @Mock
    private RealTimeMultiplayer mMultiplayer;
    private Rules mRules = new RussianRules();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        FragmentManager fm = mock(FragmentManager.class);
        when(activity.getFragmentManager()).thenReturn(fm);
        when(fm.beginTransaction()).thenReturn(mock(FragmentTransaction.class));
        when(activity.getAssets()).thenReturn(mock(AssetManager.class));

        Dependencies.inject(mMultiplayer);
        Dependencies.inject(mRules);
        Dependencies.inject(mock(GameSettings.class));
    }

    @Test
    public void WhenScreenDestroyed_ForAndroidGame__AndroidOpponentIsCancelled() {
        AiOpponent android = mock(AiOpponent.class);
        Session session = new Session(mock(PlayerOpponent.class), android);
        Game game = new AndroidGame();
        OnlineGameScreen screen = new GameplayScreen(activity, game, session, mock(TurnTimerController.class));

        screen.onDestroy();

        verify(android, times(1)).cancel();
    }
}