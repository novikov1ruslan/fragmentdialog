package com.ivygames.morskoiboi.invitations;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.ivygames.morskoiboi.ApiClient;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvitationManager {

    @NonNull
    private final Set<String> mIncomingInvitationIds = new HashSet<>();
    @NonNull
    private final OnInvitationReceivedListener mInvitationListener = new OnInvitationReceivedListenerImpl();
    @NonNull
    private final InvitationLoadListener mResultCallback = new LoadInvitationsResultResultCallback();
    @NonNull
    private final List<InvitationReceivedListener> mInvitationReceivers = new ArrayList<>();

    @NonNull
    private final ApiClient mGoogleApiClient;

    public InvitationManager(@NonNull ApiClient client) {
        mGoogleApiClient = client;
    }

    public void registerInvitationReceiver(@NonNull InvitationReceivedListener receiver) {
        mInvitationReceivers.add(receiver);
        Ln.d(receiver + " registered as invitation receiver");
    }

    public void unregisterInvitationReceiver(@NonNull InvitationReceivedListener receiver) {
        mInvitationReceivers.remove(receiver);
        Ln.d(receiver + " unregistered as invitation receiver");
    }

    public void loadInvitations() {
        if (!mGoogleApiClient.isConnected()) {
            Ln.w("API client has to be connected");
            return;
        }
        mGoogleApiClient.registerInvitationListener(mInvitationListener);

        Ln.d("loading invitations...");
        mGoogleApiClient.loadInvitations(mResultCallback);
    }

    public Set<String> getInvitations() {
        return new HashSet<>(mIncomingInvitationIds);
    }

    private void notifyReceived(@NonNull GameInvitation invitation) {
        for (InvitationReceivedListener receiver : mInvitationReceivers) {
            receiver.onInvitationReceived(invitation);
        }
    }

    private void notifyUpdated() {
        for (InvitationReceivedListener receiver : mInvitationReceivers) {
            receiver.onInvitationsUpdated(new HashSet<>(mIncomingInvitationIds));
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

    private class LoadInvitationsResultResultCallback implements InvitationLoadListener {

        @Override
        public void onResult(@NonNull Collection<GameInvitation> invitations) {
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
