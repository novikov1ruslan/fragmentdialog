package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.InvitationLoadListener;
import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.invitations.InvitationListener;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class InvitationManager {

    @NonNull
    private final Set<String> mIncomingInvitationIds = new HashSet<>();
    @NonNull
    private final List<InvitationListener> mInvitationListeners = new ArrayList<>();

    @NonNull
    private final ApiClient mApiClient;

    InvitationManager(@NonNull ApiClient client) {
        mApiClient = client;
    }

    void addInvitationListener(@NonNull InvitationListener listener) {
        mInvitationListeners.add(listener);
        Ln.v(listener + " registered as invitation listener");
        listener.onInvitationsUpdated(getInvitationIds());
    }

    void removeInvitationReceiver(@NonNull InvitationListener listener) {
        mInvitationListeners.remove(listener);
        Ln.v(listener + " unregistered as invitation listener");
    }

    public void loadInvitations() {
        if (!mApiClient.isConnected()) {
            Ln.e("API client has to be connected");
            return;
        }

        mApiClient.registerInvitationListener(new OnInvitationReceivedListenerImpl());

        Ln.v("loading invitations...");
        mApiClient.loadInvitations(new InvitationLoadListenerImpl());
    }

    @NonNull
    Set<String> getInvitationIds() {
        return new HashSet<>(mIncomingInvitationIds);
    }

    private void notifyReceived(@NonNull GameInvitation invitation) {
        for (InvitationListener listener : mInvitationListeners) {
            listener.onInvitationReceived(invitation);
        }
    }

    private void notifyUpdated() {
        for (InvitationListener listener : mInvitationListeners) {
            listener.onInvitationsUpdated(new HashSet<>(mIncomingInvitationIds));
        }
    }

    private class OnInvitationReceivedListenerImpl implements OnInvitationReceivedListener {

        @Override
        public void onInvitationReceived(Invitation invitation) {
            String displayName = invitation.getInviter().getDisplayName();
            Ln.d("received invitation from: " + displayName);

            String invitationId = invitation.getInvitationId();
            mIncomingInvitationIds.add(invitationId);
            notifyReceived(new GameInvitation(displayName, invitationId));
        }

        @Override
        public void onInvitationRemoved(String invitationId) {
            Ln.d("invitationId=" + invitationId + " withdrawn");
            mIncomingInvitationIds.remove(invitationId);
            notifyUpdated();
        }

    }

    private class InvitationLoadListenerImpl implements InvitationLoadListener {

        @Override
        public void onLoaded(@NonNull Collection<GameInvitation> invitations) {
            mIncomingInvitationIds.clear();
            if (invitations.size() > 0) {
                Ln.v("loaded " + invitations.size() + " invitations");
                for (GameInvitation invitation: invitations) {
                    mIncomingInvitationIds.add(invitation.id);
                }
            } else {
                Ln.d("no invitations");
            }
            notifyUpdated();
        }
    }
}
