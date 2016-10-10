package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.invitations.InvitationLoadListener;
import com.ivygames.common.multiplayer.MultiplayerImpl;
import com.ivygames.morskoiboi.idlingresources.TaskResource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvitationProxy {

    private TaskResource setInvitation;
    private TaskResource sendInvitation;

    private InvitationApiClient invitationApiClient = new InvitationApiClient();

    public void init() {
        Dependencies.inject(new MultiplayerImpl(invitationApiClient, 1000));
    }

    public void destroy() {
        if (setInvitation != null) {
            unregisterIdlingResources(setInvitation);
        }

        if (sendInvitation != null) {
            unregisterIdlingResources(sendInvitation);
        }
    }

    public final void sendInvitation(final String displayName, final String invitationId) {
        sendInvitation = new TaskResource(new Runnable() {
            @Override
            public void run() {
                invitationApiClient.receiveInvitation(displayName, invitationId);
            }
        });
        registerIdlingResources(sendInvitation);
    }

    public final void setInvitations(@NonNull final Set<String> invitations) {
        setInvitation = new TaskResource(new Runnable() {
            @Override
            public void run() {
                invitationApiClient.loadInvitations(invitations);
            }
        });
        registerIdlingResources(setInvitation);
    }

    public static class InvitationApiClient extends DummyApiClient {

        private OnInvitationReceivedListener listener;
        private Set<String> invitations = new HashSet<>();
        private InvitationLoadListener loadListener;

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
    private static Collection<GameInvitation> createGameInvitations(Set<String> invitations) {
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
