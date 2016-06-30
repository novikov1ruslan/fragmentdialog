package com.ivygames.morskoiboi.invitations;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Invitations;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.ivygames.morskoiboi.ApiClient;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvitationManager {

    @NonNull
    private final Set<String> mIncomingInvitationIds = new HashSet<>();
    @NonNull
    private final OnInvitationReceivedListener mInvitationListener = new OnInvitationReceivedListenerImpl();
    @NonNull
    private final ResultCallback<Invitations.LoadInvitationsResult> mResultCallback = new LoadInvitationsResultResultCallback();
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
        mGoogleApiClient.loadInvitations().setResultCallback(mResultCallback);
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

    private class LoadInvitationsResultResultCallback implements ResultCallback<Invitations.LoadInvitationsResult> {

        @Override
        public void onResult(@NonNull Invitations.LoadInvitationsResult list) {
            mIncomingInvitationIds.clear();
            if (list.getInvitations().getCount() > 0) {
                InvitationBuffer invitations = list.getInvitations();
                Ln.v("loaded " + invitations.getCount() + " invitations");
                for (int i = 0; i < invitations.getCount(); i++) {
                    Invitation invitation = invitations.get(i);
                    String invitationId = invitation.getInvitationId();
//                    if (mIncomingInvitationIds.contains(invitationId)) {
//
//                    } else {
                    mIncomingInvitationIds.add(invitationId);
                    notifyReceived(invitation);
//                    }
                }
                list.getInvitations().release();
            } else {
                Ln.v("no invitations");
            }

        }
    }
}