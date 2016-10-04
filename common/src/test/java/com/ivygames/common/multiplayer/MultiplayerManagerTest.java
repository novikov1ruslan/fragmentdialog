package com.ivygames.common.multiplayer;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.ivygames.common.googleapi.ApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class MultiplayerManagerTest {

    private MultiplayerManager mm;
    @Mock
    private ApiClient apiClient;
    @Mock
    private MultiplayerListener multiplayerListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mm = new MultiplayerManager(apiClient);
        mm.setListener(multiplayerListener);
    }

    @Test
    public void WhenResultOkOnInvitingPlayers__RoomIsCreated() {
        RoomListener roomListener = mock(RoomListener.class);
        RealTimeMessageReceivedListener rtListener = mock(RealTimeMessageReceivedListener.class);
        mm.invitePlayers(1, roomListener, rtListener);
        Intent intent = new Intent();
        ArrayList<String> list = new ArrayList<>(Collections.singletonList("1"));
        intent.putStringArrayListExtra(Games.EXTRA_PLAYER_IDS, list);
        intent.putExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 1);
        intent.putExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 2);

        mm.handleResult(1, Activity.RESULT_OK, intent);

        verify(apiClient, times(1)).createRoom(list, 1, 2, roomListener, rtListener);
    }

    @Test
    public void WhenInvitingPlayersResultIsNotOk__RoomIsNotCreated() {
        RoomListener roomListener = mock(RoomListener.class);
        RealTimeMessageReceivedListener rtListener = mock(RealTimeMessageReceivedListener.class);
        mm.invitePlayers(1, roomListener, rtListener);
        Intent intent = mock(Intent.class);

        mm.handleResult(1, Activity.RESULT_CANCELED, intent);

        verify(apiClient, never()).createRoom(any(ArrayList.class), anyInt(), anyInt(),
                any(RoomListener.class), any(RealTimeMessageReceivedListener.class));
        verify(multiplayerListener, times(1)).invitationCanceled();
    }

    @Test
    public void WhenInvitationFromInboxAccepted_PlayerJoinsTheRoom() {
        RoomListener roomListener = mock(RoomListener.class);
        RealTimeMessageReceivedListener rtListener = mock(RealTimeMessageReceivedListener.class);
        mm.showInvitations(1, roomListener, rtListener);
        Intent intent = new Intent();
        Invitation invitation = mock(Invitation.class);
        intent.putExtra(Multiplayer.EXTRA_INVITATION, invitation);

        mm.handleResult(1, Activity.RESULT_OK, intent);

        verify(apiClient, times(1)).joinRoom(invitation, roomListener, rtListener);
    }

    @Test
    public void WhenInvitationFromInboxNotAccepted_PlayerFailsToJoinTheRoom() {
        RoomListener roomListener = mock(RoomListener.class);
        RealTimeMessageReceivedListener rtListener = mock(RealTimeMessageReceivedListener.class);
        mm.showInvitations(1, roomListener, rtListener);
        Intent intent = new Intent();
        Invitation invitation = mock(Invitation.class);
        intent.putExtra(Multiplayer.EXTRA_INVITATION, invitation);

        mm.handleResult(1, Activity.RESULT_CANCELED, intent);

        verify(apiClient, never()).joinRoom(any(Invitation.class),
                any(RoomListener.class), any(RealTimeMessageReceivedListener.class));
        verify(multiplayerListener, times(1)).invitationCanceled();
    }

}