package com.ivygames.common.multiplayer;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.invitations.InvitationListener;
import com.ivygames.common.invitations.InvitationLoadListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(apiClient.isConnected()).thenReturn(true);

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
    public void WhenInvitationFromInboxAccepted__PlayerJoinsTheRoom() {
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
    public void WhenInvitationFromInboxNotAccepted__PlayerFailsToJoinTheRoom() {
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

    @Test
    public void WhenWaitingForOpponentSucceeds__GameStarts() {
        Room room = mock(Room.class);
        mm.showWaitingRoom(1, room);
        Intent intent = mock(Intent.class);

        mm.handleResult(1, Activity.RESULT_OK, intent);

        verify(multiplayerListener, times(1)).gameStarted();
    }

    @Test
    public void WhenWaitingForOpponentFails__PlayerLeft() {
        Room room = mock(Room.class);
        mm.showWaitingRoom(1, room);
        Intent intent = mock(Intent.class);

        mm.handleResult(1, GamesActivityResultCodes.RESULT_LEFT_ROOM, intent);

        verify(multiplayerListener, times(1)).playerLeft();
    }

    @Test
    public void WhenCancellingWaitingForOpponent__PlayerLeft() {
        Room room = mock(Room.class);
        mm.showWaitingRoom(1, room);
        Intent intent = mock(Intent.class);

        mm.handleResult(1, Activity.RESULT_CANCELED, intent);

        verify(multiplayerListener, times(1)).playerLeft();
    }

    @Test
    public void WhenQuickGameSelected__RoomIsCreated() {
        RoomListener roomListener = mock(RoomListener.class);
        RealTimeMessageReceivedListener rtListener = mock(RealTimeMessageReceivedListener.class);
        mm.quickGame(roomListener, rtListener);

        verify(apiClient, times(1)).createRoom(1, 1, roomListener, rtListener);
    }

    @Test
    public void WhenInvitationsLoaded__() {
        mm.loadInvitations();

        verify(apiClient, times(1)).registerInvitationListener(any(OnInvitationReceivedListener.class));
        verify(apiClient, times(1)).loadInvitations(any(InvitationLoadListener.class));
    }

    @Test
    public void WhenInvitationsAlreadyLoaded__AskingToLoadThemReturnRightAway() {
        InvitationListener listener = mock(InvitationListener.class);
        Set<String> names = Collections.singleton("Sagi");
        ApiClient apiClient = new InvitationApiClient(names);
        mm = new MultiplayerManager(apiClient);

        mm.addInvitationListener(listener);
        mm.loadInvitations();

        verify(listener, times(1)).onInvitationsUpdated(names);
    }

    @Test
    public void WhenListenerRemoved__ItIsNotCalled() {
        InvitationListener listener = mock(InvitationListener.class);
        Set<String> names = Collections.singleton("Sagi");
        ApiClient apiClient = new InvitationApiClient(names);
        mm = new MultiplayerManager(apiClient);

        mm.addInvitationListener(listener);
        reset(listener);
        mm.removeInvitationListener(listener);
        mm.loadInvitations();

        verify(listener, never()).onInvitationsUpdated(any(Set.class));
    }

    @Test
    public void WhenInvitationsLoaded__TheyAreReceived() {
        InvitationListener listener = mock(InvitationListener.class);
        Set<String> names = Collections.singleton("Sagi");
        InvitationApiClient apiClient = new InvitationApiClient(names);
        mm = new MultiplayerManager(apiClient);

        mm.addInvitationListener(listener);
        mm.loadInvitations();

        apiClient.receiveInvitation("Sagi", "1");
        verify(listener, times(1)).onInvitationReceived(new GameInvitation("Sagi", "1"));

        reset(listener);
        apiClient.loadInvitations(names);
        verify(listener, times(1)).onInvitationsUpdated(names);
    }

    public static class InvitationApiClient extends DummyApiClient {

        private OnInvitationReceivedListener listener;
        private Collection<String> invitations;
        private InvitationLoadListener loadListener;

        InvitationApiClient(Collection<String> invitations) {
            this.invitations = invitations;
        }

        @Override
        public void registerInvitationListener(@NonNull OnInvitationReceivedListener listener) {
            this.listener = listener;
        }

        @Override
        public void loadInvitations(@NonNull InvitationLoadListener listener) {
            loadListener = listener;
            listener.onLoaded(createGameInvitations(invitations));
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        private void receiveInvitation(String displayName, String invitationId) {
            listener.onInvitationReceived(createInvitation(displayName, invitationId));
        }

        private void loadInvitations(Set<String> invitations) {
            this.invitations = invitations;
            loadListener.onLoaded(createGameInvitations(invitations));
        }

    }

    @NonNull
    private static Collection<GameInvitation> createGameInvitations(Collection<String> invitations) {
        Collection<GameInvitation> invitationsCopy = new HashSet<>();
        for (String id : invitations) {
            invitationsCopy.add(new GameInvitation("Sagi " + id, id));
        }
        return invitationsCopy;
    }

    @NonNull
    private static Invitation createInvitation(String displayName, String invitationId) {
        Invitation invitation = mock(Invitation.class);
        Participant inviter = mock(Participant.class);
        when(invitation.getInviter()).thenReturn(inviter);
        when(inviter.getDisplayName()).thenReturn(displayName);
        when(invitation.getInvitationId()).thenReturn(invitationId);
        return invitation;
    }
}