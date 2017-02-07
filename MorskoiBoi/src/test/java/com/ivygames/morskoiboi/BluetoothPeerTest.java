package com.ivygames.morskoiboi;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.bluetooth.peer.BluetoothPeer;
import com.ivygames.bluetooth.peer.ConnectionLostListener;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class BluetoothPeerTest {

    private BattleshipActivity activity;
    @Mock
    private Game game;
    @Mock
    private BluetoothPeer mBluetoothPeer;

    private Session session;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        session = new Session(mock(PlayerOpponent.class), mock(Opponent.class));
        activity = Robolectric.buildActivity(BattleshipActivity.class).get();
        activity.onCreate(null);

        Dependencies.inject(mBluetoothPeer);
    }

    @Test
    public void WhenNextRoundChosenDuringLostScreen__EventCalled() {
        OnlineGameScreen screen = new LostScreen(activity, game, session);
        activity.setScreen(screen);
        reset(mBluetoothPeer);
        InOrder inOrder = inOrder(mBluetoothPeer);

        activity.findViewById(R.id.yes_button).performClick();

        inOrder.verify(mBluetoothPeer).resetConnectionLostListener();
        inOrder.verify(mBluetoothPeer).setConnectionLostListener(any(ConnectionLostListener.class));
    }

}