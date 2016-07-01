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
    private final List<OnInvitationReceivedListener> mInvitationReceivers = new ArrayList<>();

    @NonNull
    private final ApiClient mGoogleApiClient;

    public InvitationManager(@NonNull ApiClient client) {
        mGoogleApiClient = client;
    }

    public void registerInvitationReceiver(@NonNull OnInvitationReceivedListener receiver) {
        mInvitationReceivers.add(receiver);
    }

    public void unregisterInvitationReceiver(@NonNull OnInvitationReceivedListener receiver) {
        mInvitationReceivers.remove(receiver);
    }

    public void loadInvitations() {
        if (!mGoogleApiClient.isConnected()) {
            Ln.w("API client has to be connected");
            return;
        }
        Ln.d("load invitations");
        mGoogleApiClient.registerInvitationListener(mInvitationListener);

        Ln.d("loading invitations...");
        mGoogleApiClient.loadInvitations(mResultCallback);
    }

    public Set<String> getInvitations() {
        return new HashSet<>(mIncomingInvitationIds);
    }

    private void notifyReceived(@NonNull Invitation invitation) {
        for (OnInvitationReceivedListener receiver : mInvitationReceivers) {
            receiver.onInvitationReceived(invitation);
        }
    }

    private void notifyRemoved(@NonNull String invitationId) {
        for (OnInvitationReceivedListener receiver : mInvitationReceivers) {
            receiver.onInvitationRemoved(invitationId);
        }
    }

    private class OnInvitationReceivedListenerImpl implements OnInvitationReceivedListener {

        @Override
        public void onInvitationReceived(Invitation invitation) {
            String displayName = invitation.getInviter().getDisplayName();
            Ln.d("received invitation from: " + displayName);
            mIncomingInvitationIds.add(invitation.getInvitationId());
            notifyReceived(invitation);
        }

        @Override
        public void onInvitationRemoved(String invitationId) {
            Ln.d("invitationId=" + invitationId + " withdrawn");
            mIncomingInvitationIds.remove(invitationId);
            notifyRemoved(invitationId);
        }
    }

    private class LoadInvitationsResultResultCallback implements InvitationLoadListener {

        @Override
        public void onResult(@NonNull Collection<Invitation> invitations) {
            mIncomingInvitationIds.clear();
            if (invitations.size() > 0) {
                Ln.v("loaded " + invitations.size() + " invitations");
                for (Invitation invitation: invitations) {
                    String invitationId = invitation.getInvitationId();
                    mIncomingInvitationIds.add(invitationId);
                    notifyReceived(invitation);
                }
            } else {
                Ln.v("no invitations");
            }

        }
    }
}